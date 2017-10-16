package com.choudoufu.algorithm.compress;

/**
 * Created by xuhaowende on 2017/10/17.
 */

import com.choudoufu.algorithm.BaseClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FastLZ extends BaseClient{

/*
    MAX_COPY = 32;
    MAX_LEN = 264; //256 + 8
    HASH_LOG = 13;
    HASH_SIZE = (1<< HASH_LOG); // 8192
    HASH_MASK = (HASH_SIZE-1); // 8191
    MAX_DISTANCE_LZ1 = 8192;
    MAX_DISTANCE_LZ2 = 8191;
    MAX_FARDISTANCE_LZ2 = (65535+MAX_DISTANCE_LZ2-1); // 73725
*/

    // This is the C version converted into Java
    private static final int VERSION = 0x000100;
    private static final String VERSION_STRING = "0.1.0";

    // A bit pointless but I wanted to use a enum! So There
    enum defs {
        FASTLZ0(0), FASTLZ1(1), FASTLZ2(2);
        private int value;

        private defs(int value) {
            this.setValue(value); // I DO WHAT I WANT!
        }

        public int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }
    } // I'M GOING TO HAVE THIS ENUM!

    public int getVersion() {
        return FastLZ.VERSION;
    }

    public String getVersionString() {
        return FastLZ.VERSION_STRING;
    }

    static int readU16(byte[] in, int offset) {
        if(offset + 1 >= in.length)
            return in[offset] & 0xff;
        return (in[offset] & 0xff) + ((in[offset+1] & 0xff) << 8);
    }

    static int hashFunction(byte[] in, int offset) {
        int v = readU16(in, offset);
        v ^= readU16(in, offset + 1) ^ (v >>> (3)); // (16 - HASH_LOG)
        v &= 8191; //HASH_MASK;
        return v;
    }

    static defs findLevel(byte[] in) {
        byte level = (byte) ((in[0] >>> 5) + 1);
        if(level == 1) return defs.FASTLZ1;
        else if (level == 2) return defs.FASTLZ2;
        else return defs.FASTLZ0;
    }

    public static int calcLength(int length) {
        return (int) Math.max(66, (length * 1.06));
    }

    public static byte[] compress(byte[] in) {
        byte[] out = new byte[calcLength(in.length)];
        int size = compress(in, out);
        return Arrays.copyOf(out, size);
    }

    public static byte[] compress(String in) {
        byte[] out = new byte[calcLength(in.length())];
        int size = compress(in.getBytes(), out);
        return Arrays.copyOf(out, size);
    }

    public static int compress(byte[] in, byte[] out) {
        if(in.length < 65536) return compress_lz(in, out, defs.FASTLZ1);
        else return compress_lz(in, out, defs.FASTLZ2);
    }

    static int compress_lz(byte[] in, byte[] out, defs compression_level) {
        int ip = 0;
        int op = 0;
        int ipBound = in.length-2;
        int ipLimit = in.length-12;
        int[] htab = new int[8192]; // HASH_SIZE
        int hslot;
        int hval;
        int copy;

        if(in.length < 4) {
            if(in.length != 0) {
                out[op++] = (byte) (in.length-1);
                ipBound++;
                while(ip <= ipBound) out[op++] = in[ip++];
                return in.length+1;
            } else return 0;
        }

        for(hslot = 0; hslot < 8192; hslot++) htab[hslot] = ip; // hslot < HASH_SIZE;

        copy = 2;
        out[op++] = 31;//MAX_COPY-1;
        out[op++] = in[ip++];
        out[op++] = in[ip++];

        while(ip < ipLimit) {
            int ref = 0;
            int distance = 0;
            int len = 3;
            int anchor = ip;
            boolean labelMatch = false;

            if(compression_level == defs.FASTLZ2)
                if(in[ip] == in[ip-1] && readU16(in, ip-1) == readU16(in, ip+1)) {
                    distance = 1;
                    ip += 3;
                    ref = anchor-1+3;
                    labelMatch = true;
                }
            if(!labelMatch) {
                hval = hashFunction(in, ip);
                hslot = hval;
                ref = htab[hval];
                distance = anchor-ref;
                htab[hslot] = anchor;

                if(distance == 0 || (compression_level == defs.FASTLZ1 ? distance >= 8192 : distance >= 73725) //distance >= MAX_DISTANCE_LZ1 : distance >= MAX_FARDISTANCE_LZ2)
                        || in[ref++] != in[ip++]
                        || in[ref++] != in[ip++]
                        || in[ref++] != in[ip++]) {
                    out[op++] = in[anchor++];
                    ip = anchor;
                    copy++;
                    if(copy == 32) { // MAX_COPY
                        copy = 0;
                        out[op++] = 31; //MAX_COPY-1;
                    } continue;
                }

                if(compression_level == defs.FASTLZ2)
                    if(distance >= 8191) { // MAX_DISTANCE_LZ2
                        if(in[ip++] != in[ref++] || in[ip++] != in[ref++]) {
                            out[op++] = in[anchor++];
                            ip = anchor;
                            copy++;
                            if(copy == 32) { // MAX_COPY
                                copy = 0;
                                out[op++] = 31; //MAX_COPY-1;
                            } continue;
                        }
                        len += 2;
                    }
            } //labelMatch
            ip = anchor + len;
            distance--;

            if(distance == 0) {
                byte x = in[ip - 1];
                while(ip < ipBound)
                    if(in[ref++] != x) break; else ip++;
            } else
                for(;;) {
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    if (in[ref++] != in[ip++]) break;
                    while(ip < ipBound) if (in[ref++] != in[ip++]) break;
                    break;
                }

            if(copy != 0) out[op-copy-1] = (byte) (copy-1); else op--;

            copy = 0;
            ip -= 3;
            len = ip - anchor;

            if(compression_level == defs.FASTLZ2)
                if(distance < 8191) // MAX_DISTANCE_LZ2
                    if(len < 7) {
                        out[op++] = (byte) ((len << 5) + (distance >>> 8));  // >>>
                        out[op++] = (byte) (distance & 255);
                    } else {
                        out[op++] = (byte) ((7 << 5) + (distance >>> 8)); // >>>
                        for(len -= 7; len >= 255; len -= 255) out[op++] = (byte) 255;
                        out[op++] = (byte) len;
                        out[op++] = (byte) (distance & 255);
                    }
                else
                if(len < 7) {
                    distance -= 8191; // MAX_DISTANCE_LZ2
                    out[op++] = (byte) ((len << 5) + 31);
                    out[op++] = (byte) 255;
                    out[op++] = (byte) (distance >>> 8);  // >>>
                    out[op++] = (byte) (distance & 255);
                } else {
                    distance -= 8191; // MAX_DISTANCE_LZ2
                    out[op++] = (byte) ((7 << 5) + 31);
                    for(len -= 7; len >= 255; len -= 255) out[op++] = (byte) 255;
                    out[op++] = (byte) len;
                    out[op++] = (byte) 255;
                    out[op++] = (byte) (distance >>> 8);  // >>>
                    out[op++] = (byte) (distance & 255);
                }
            else {
                if(len > 262) // MAXLEN - 2
                    while (len > 262) { //  MAX_LEN - 2
                        out[op++] = (byte) ((7 << 5) + (distance >>> 8)); // >>>
                        out[op++] = (byte) 253; // (MAX_LEN - 2 - 7 - 2); // MAX_LEN - 11
                        out[op++] = (byte) (distance & 255);
                        len -= 262; //MAX_LEN - 2;
                    }

                if(len < 7) {
                    out[op++] = (byte) ((len << 5) + (distance >>> 8)); // >>>
                    out[op++] = (byte) (distance & 255);
                } else {
                    out[op++] = (byte) ((7 << 5) + (distance >>> 8)); // >>>
                    out[op++] = (byte) (len - 7);
                    out[op++] = (byte) (distance & 255);
                }
            }

            hval = hashFunction(in, ip);
            htab[hval] = ip++;

            hval = hashFunction(in, ip);
            htab[hval] = ip++;

            out[op++] = 31; //MAX_COPY-1;

            continue;
        }

        ipBound++;
        while(ip <= ipBound) {
            out[op++] = in[ip++];
            copy++;
            if(copy == 32) { //MAX_COPY
                copy = 0;
                out[op++] = 31; //MAX_COPY-1;
            }
        }

        if(copy != 0) out[op-copy-1] = (byte) (copy-1); else op--;

        if(compression_level == defs.FASTLZ2) out[0] |= 1 << 5;

        return op;
    }

    /* All of these use size_output to leave it up whoever uses this
     * library to figure that stuff out. :)
     *
     * Will throw exception if there is not enough space to decompress
     */

    public static String decompressToString(byte[] in, int size_output) throws Exception {
        byte[] result = decompress(in, size_output);
        return new String(result, StandardCharsets.US_ASCII);
    }

    public static byte[] decompress(byte[] in, int size_output) throws Exception {
        byte[] out = new byte[size_output];
        int result = decompress(in, out);
        return Arrays.copyOf(out, result);
    }

    public static int decompress(byte[] in, byte[] out) throws Exception {
        return decompress(in, out, findLevel(in));
    }

    static int decompress(byte[] in, byte[] out, defs compression_level) throws Exception {
        if(compression_level == defs.FASTLZ0) throw new Exception("WTF?!");
        int ip = 0;
        int op = 0;
        long ctrl = in[ip++] & 31;

        boolean loop = true;
        do {
            int ref = op;
            long len = ctrl >>> 5;
            long ofs = (ctrl & 31) << 8;

            if(ctrl >= 32) {
                int code;
                len--;
                ref -= ofs;

                if(len == 6) { // (len == 7-1)
                    if(compression_level == defs.FASTLZ1) len += in[ip++] & 0xff;
                    else
                        do {
                            code = in[ip++] & 0xff;
                            len += code;
                        } while(code == 255);
                }
                if(compression_level == defs.FASTLZ1) ref -= in[ip++] & 0xff;
                else {
                    code = in[ip++] & 0xff;
                    ref -= code;

                    if(code == 255 && ofs == 31 << 8) {
                        ofs = (in[ip++] & 0xff) << 8;
                        ofs += in[ip++] & 0xff;

                        ref = (int) (op-ofs-8191); // MAX_DISTANCE_LZ2
                    }
                }

                if(op+len+3 > out.length) throw new Exception("(op+len+3 > out.length)");
                if(ref-1 < 0) throw new Exception("(ref-1 < 0)");

                if(ip < in.length) ctrl = in[ip++] & 0xff; else loop = false;

                if(ref == op) {
                    byte b = out[ref-1];
                    out[op++] = b;
                    out[op++] = b;
                    out[op++] = b;

                    for(;len != 0; --len) out[op++] = b;
                } else {
                    ref--;

                    out[op++] = out[ref++];
                    out[op++] = out[ref++];
                    out[op++] = out[ref++];

                    for(;len != 0; --len) out[op++] = out[ref++];
                }
            } else {
                ctrl++;

                if(op+ctrl > out.length) throw new Exception("(op+ctrl > out.length)");
                if(ip+ctrl > in.length) throw new Exception("(ip+ctrl > in.length)");

                out[op++] = in[ip++];

                for(--ctrl; ctrl != 0; ctrl--) out[op++] = in[ip++];

                loop = ip < in.length ? true : false;
                if(loop) ctrl = in[ip++] & 0xff;
            }
        } while(loop);

        return op;
    }

    private static final int SIG_COUNT = 3;


    public static void main(String[] args) throws Exception {
        String input = "8d2860ad03da21ac94f30918d45b41a2666082997f5ddd52fa327a903e8d477d6a2a25679a7327aa9e37b2aae6155ba0f701ea438ce6a2e41cbc416ca602d92030b5c57151e9f06e3cf9d4a01088c39708c220af97eb7fcb86144f83e022c07eb6d10991e4e486a53b62622c3dd79770ac6b09e8d5157778ccdc6e9990e405d1be299878e8431bf702cda27be746bdc6571c395a77480408e8ef5cb2b53892980aa93054452f1e5772df838124c7cc8588e5aa82c453945a479a5def0e1a589972736d0f1735145bbf309645f0793984e6d99584a54f69570d182ad39e30cd1ae9e05bd940e852e9e605afcd00e048fef0e53d169a196034c00105e25ec121c60a7a4393b3b8ab2eddc03b4d90a5db305336ba4061c32fd2a626cd6f1948339105b488999098aa8bb1bbce1035396453ef85c2d430d223872880a65de52a305705a009b0cc48a8fa3caad3af18e8cf9a2db47c64545b3d16a1dbc888435d226fb1918aff7659dd14bbf74ef6d75d879dfed95a872be009877114922c4c9f4cfe3b95572e1202afa648a7f41430d5ed01bb51f4526ec35baae9ab4092d71ae87460d8ee0729399ff26c8253ef1a94f8c4f571ed34b951c8e899ea3584246ba0333584fd765f1e6ee0b63e21ff9418fd9c8921b743e9747a957e8f46cc097c64fda3168d2fa48e7453b126159e8c72aee6d1d9e5bf1943f417122e44e899dfd9c9ee9cb27ec6d3ba38168e3b44d393c5f28a0aecf2f162aa007e41e04670edfb3d4449a27b9764b73766ab39af7fad97b082e0639ce34f786e1d2c86de3ae87c8bcc02419a3615fd9a20584455183364763b95c5027dc31c122bcfa2d8b81b055b816efdf58cc89fb11c48b5bc630d7682c80b6cec83990c19bae9ba1142f33ebd29c23d8161dd589062829521be6fe387eb3a74fbf3f818070bf5743ed630831006235118ade4a347d10cd5eba7b03df31938fe2fa110caf90496df9a2eb1b5524f047e0074af3567cd3cfcd35e1b5a8de99b468fa2f6edee2c5f463a2924c21a544eef9b5d5be0b5dce27186d8120fcec6606d78aa66e09d709d3924003b845b5408c5a849c0e6921dc91a25048660100deaf3424acbb6e8e081b272e92bedd6abb0260248c80d5c9b164db02b19d7b8d9ff3155b21f2892ec735debdbbc48c75cf9fef6e6f3fb29cb290c543d816c7b8e9b2bbd256e721717e75761a761ffe789f053a13e7a12e4f72acf4735aa3f44e65e5e6f7373723f3a685aeadbb501a68278d67c258e3849d1cccda70a1a16c3705a877a48114a57dfc4c278448a5b1ed5387a8ec0ef8b4a4c7bddfdc8c528823be7220cc07847e415990ee39c2b01921fbc321fe31a73c69fce2e18418e879de90a228ed5d761b7701bac76854f5285254ad65b24ce927d390cb209b87d2a367f670a0353745616a1d4e24e5bfa2ed61c1d2606885369004fa0494b4d2fa149f82203eeed50d678d4d3310b2f8aa99558100ae95e84c042cd19a95cd27912186da5b5364efa8f11e0821e995288c88f30e40b96fffa27580e3da92dcd5997e01237b890746b35b3751a8fbe9ee6fdf8d06285c7b9237b8d50c0ec0b7d33da75770a345ed4b6bf918e9ffeedc846d6ab19bcccfa996e90563951f8b605d75b21f1ba7da88e2031415f99e62900d56ebe6fd227a60e3039d6173cdb0fa3b41765e7fbc95b3be80dcda2ab259cda71e444bc13e0aca8e7afcf623a3c1b8ec0e0129af5c9f73cfa218b81241cb866085c70574156ee2b26ceee524dc1f0ca2d8f9e5e5223e8b1c4bbe4b87ac944a73e8aabd8e2161c1459400c20707561bf4001143cd5ee8a8d50428727621bce96073f62f796f41d6c549e075115f0c6255eee8096f843c48f70a8f01f685a7cdf176bda71dfd2c4d7ecfa461a62753f805174ccf4312326189931f5fec0e4146568df896af18dc0d1bdf5449a9e7f5584e51d8c5d98ced1d84ee2f91141fb5454a9f3ef6f38616deb95e37014eca8183a35a25edc665d5394f53ba93810e366ce2df2c5cd138d7f9404079bead4aa4a1dce3b434ded1b7979a752742b03e4df8a5f26375339412d548a54d45972ef11acd0ca68f2c4db14399e06e5efdcb3ce3e0e3e6e6b0133492ac13a71947c623207a6dab400fe97ec893446b1661decfe507962dd911df6b810ef8201be7a2472499ae6ab04c9c62f9dabf20ed33310aecca63524f7cbee9a3286386a8dee1ceee7dac04e234d5d12d13c642943843ea99b110409c56bfd62be95b35c09d9a2e7d35b7777976a079972ae9f88d12ef32b0b98d4e5c3a8d798c448aae6201770cd79ee17fc9bb2d0b63beec5f6c51cde5960c063a86017c0589b592e2034b23f0da897a114cb0286a31db1e2e20a589d55884d2e3216450f4ba313de5030ec6d360edb94f6ecddd6f277fe4fdbc7c92e19e6a93150dea267dd5a9d437e04ec10e80396c80314b253b817298f7dc976e56902896d2d03f555f98c93b54f6212b287fd8143d7135cc83b6f35ac4ba48bcf908a96b979f353c443012bc116bfa3832815779beb9e9d8412955ec53de01831378310c79a969e233cd51f62eb4f5aa6e5d3c49c07cd27a09410a71c5ab2bf9318337ad7a2faa07a52a3b43bcd59c1bdfcf9795ed67fa561c39a48b631212ec1f30833c07e3d3e79f51adf5e8f3aba16148a3cc6f7ae594d5cce5e9e3b4a99f1f601ade835e80a76295ba9416cb68cf22ca58110ca05bdd7537b14cd43efb1945a442b9d37789f0fa7ba630d1837d862f99d6ef4d6ea82bbb1cb255f67bb3151f61729507cd84e00d74564f8d32e49c0b047792fec8e49022d2e17aae1899fad74f5ab38c7bd195b840c9dac6e72faf7705c5b7f83ab83d00b08bb1f966d40250a54621e50daf116c47c4f370521d3457c6f53feeb7790152a3754147298d68781a74004c7bd70e30888a47a30eed8f8be277dfe862fc0be58b571b67189a0d44db9fb79adeee5182dc8cbb191cecd46394d1e1c732f6576fe84c0265d5b8a7266aca6043d412f7bb5cd3bc9ee42e572337c789c9bc1df474eed568ed45fc1ddbb22c97df2a5c3ef25774ed6cde5233cb56e1c462aa28ab027f9c7b19fad8f8ce98f7b18c6f7a12721a993414327ab82cf8eb18b3c07fee5d6bb18ca58f7418b2f0840a2c843a5d58dc6e84b0b046204872a1e79535352a83021391d1031825e55116837740209ef12c73fb44eb310dc7be073b4b6e02ad11632a2230540f8bd0b579f10effdf13f4c3469820ba4bea08e795a3bff77c036651d0d691bd07de37b83c2ffe49452492372af421996f4f54cd2f2f368d816fbf0871f178bf80cb163258c529a0ad92c631320c5b1b3073fb48af498128e68095d0fc8f4ceca54a7c36a910939325129805eb9f47944edf936a501aa8f6e4db9b74b2b772218b94031be01c337cd955f149243d53f6d79f76cede7907882551e4a16fd61f5bdcd2e02167920a2a5f2df95c034012f37f3acdb7d344aa7ac937a6b4a55bba08c382535e2a74b17f0ac9686d5d581784f64f8b39652df18d7a842d9f2160439c7567d9845b9e7e59fb116d1ed5a2794a870b57f4322fa141dfe85cd816b0a485a08b3073fecf9893c7a592c1fcd70030830ef62569d569a44c7b013393ea0825dee34c25e07c983e423a431a730769ebfcb1d0157913fa09e0b78ec489a5e9bf4d212f978fddeb4eff6c241beb24c253980d33e39e2a1743e6dc9c2205dab013a522e4f9e22a41ac28f07d73faa0bc0d550b7d914a91423a4712237072d021d595f91a14bb163463e50c8b5ddbe8fa45acc8bf1a21eb0f763892f53709e7f351dc6696a3d521ec4f89b894a598f1782ddbfe608800ce7a0d7ff370e43cac149b921faf34f89f85d19096a462f047004bc9a28c1898a6c5429155ea46f1f2bed02ad52340d18c93e56e99ba967a44f65ad352aa79406920f81f1a45afc439823b49e4b21d22f365454ed46b1550301a2dd7f6193a3728e3bf523be1ba0a01ac774e151330a7dbc50a1d32d0b073896433514f5437b5758f0b033f74275d7e1e083c5520302ea71cefff72ab7111cbb3776160d8d09faa88df65cfd9e68ae3cab1307735bb4606caa6629a726db918fba914d16790f1f38a05768cf59ae98716306f7912841d3ecf77fdb941ce53c92306b601f3136d4654c85924e44da8b8f1b98b24f858ac846698a6f25614d5f3142ac46164bb1fd39e4d29c5acf49b85538ba8f99a74ba211e7859d014d74d76c9a5528122c35f4efa1a0263a4a206cd399c364ceeea0fb34e9fcce098ec950511e9bfab6c02f359c80b8cafa4470db25076bb15553486d4f0f8e886c07092f48eb83ead763f88729f3bc207b17bd81e9d8c7092987a56e95167946d08e4daa9fee1f68b24232eff4879e54b9b02618b0a53b2f30e4c3fdec3231dff84f040a705a368d15717a4ac8a2b1f3ca898044937128ef083a928cf2a44ed2499070deb3487d033d916ab9bb595ab4d3c3ffaea801f0d6538356525a8605798176f1a0f49f9ef4f69cb560d0b63cc0c8c4c0e076198900813dcb888920650f82c44de6e4500f390f24b48b8e08e5047b7fd0b0e5c226cb82863567b3f4fd7d330d355d335edb6761a54785ee8df941cde68e9645035f98ea4d14ee44eedbb12148b18e5e8e6591f6afb6e4c082a681174fbc94d24716747ce25d0484ee9aa6c52e3aaf4d3947d7f5e48782629468cf50454db8eae27c55f39c52472cca92bb87bea7efc1474aa8de1701eaa5b156d227b12af130010242cbf842fdcf10f22544b3d56e6019f95098217d9e20d610971734e361e2c8089937f1ca57daf3b85303a31305f4efda61a1b69dac280c0be25b92e238150ab56234bac88ebc41d2e459fdc2be1f289fdff560efcb18c5c9679d203be6f0c664cdf7db58055a37be8e6857f8547ec1abee8e357cacb4b07dbb80a3d1acd70d9111ed134c926930c5aa63e598cc240490ff8fba84ba64209742409c7a6137849e4c6352597674a7dcdefddd783b06357036f35cdc9d3d8097707784ad67bf0881dc953ddac2e88ffee6934d89b0cec30fc28fec3e346eb48083845f24dd81225efecba1914402cfe4da7bade3f4cac4f91b0501cce9a86c7659424c2e2951c2a7bf50754a02ae996690f46b557f0543629aa845a5d8c30a8fea9c48eb1d18fc4ecb8692c6a162e032f409b8736d77b7eeb8b0ee15905c441a61a9e3779556b0e592f2c4a58a26d7580e1fd9b3192b1785fd818c56c308c5e15422d35090c94f888b05ae6650df1c5dd11862bc8a9616efbda43d837c540275c8bb6f66347bcf246c30133ac3cedfcaa79f36c138355d8b96a30dbc1f838c76b6a79e1b02b2911d710743efa693b95768334efb16a496b25016f9af6efb70b981b4d40b5501f7ee28d022308d009c0163981bfbcfd9be3630ef0a99303e2dfcfff38c2f39afe62728ee401bb5f969656b85a1f5d94e88fed04049238643884c57c9b8af6d17dbd9c152838cffa13a382cd84a225f1456345e3d0eb39a48141293a5419ceb503c56808974cb838b1b0900a18cd4923addd44adee41c2571bcebc71732f1313fe19d2598a6b56d023e4c3b1b26e34518ee28947d4696219ed81edb535dd307fda2e5a8705bad4d3274e3ea3566c6e4acea02d5403056051045c02b456100ad0c55a9322f96087f4f0ec2d57ab341bd94b83890f683654b9fdda0d348ce8f71bb8dc9991eb9e800bd1a7fce81a28b272d3e4c41cf46c903eb9780bf8e200f497b725d9674fdc6a5925b904d992a8ce791e7f027f85ff3911d3b739720e257a942acffc23c9f9dd21dd9f3595fba859830bc4b494e2158bac04400ae4f3673082870bfe74c54960bdbff3966bc4f76f3aaf45e8ac26e6173332672d70b9e552c876a61c8a3439c392c1f3aa39c567c27f64bc5f87a8797b71cb1c761cd59e1d8df0540c2e899d126e21847bb37c6d66183b9e08481d2888512555540ffde1b936effb82dba3ef6d509739287923f0aea3889adc5ae6926091ff9621e60952ef2c9af6318bc4749f06b172d9ffe196ce6555f17ba3fd6e27afaf77737c5e9ac553ad55baf347231f605cf3767fe6ec3415647a66376b26ee6bff33939adefba7e6e723346c01987aefbab500cdf9484006748e5bcc4cd1f12c62338a2427c32e7f22715bfa4e93164ebb0971b3d73d0acff27fb85fae67d464eaa3b894953113006662628266d66847da7e53b6fcd91c5f40128e9e282968595d592434dcbca1a9f4cfbc9e26597274b516983488459f9a1da8cdc408ca1f21db177b67117ef39d59ffdcef8d25b62c7085281faa153e6a41e474f888ef234a146680c28ded391e5a4b3fe6043f8a222a5f94a3f3bcc0a18a412e6825df032f1648cf2aec7d0ed6909c8137ff38765aa7d985315f733935804fbe9d49c1fba82f727747e5c8e1012efbd65c9f61ec532688b388dc8f5ce978823c0d8efdd384f817460fe808d8fe2d6ddd3df5b3b38e9496606f485b35c75009e0556386698edf16a25ebc23018f315c371f1ab34e6442f5eb630a8ab4de7827914bfcd6d0d7757ceaf52d208b83678d1abcf9194116091e14f7b4edfea7829e0d4329c59448d4f0465d9b8200f2abadcebd69b51abbbcf0f0202403b09edbdc318f4f14e602a129532e091c66616e532773a37350df9c010ae4cd8fab86816e872e7d10b7f35e40cde1a5eb742ae940de4c7b472c1f9b0d3dabafb2d6f446c908a71edd4ddf3740d0db3c24ddac59283533d6387c3633de692b16a45e52f96582a71d23fd10167a490c093c015ec3150e1efb1829eb3c1ea5e7d5e953918fbc197c9ed807b7d246eb13b62970d2b69513285e45c03fcbb39057e8eb87eb10372e758a3f0f7cdb9eac3e2c0797e64acc09b70695c0c784806941fb7de03cb51f01a10a866717db618c5f2a737d695b8ca7124ccab98bffdac1651090f9b16eb8211ffcc480fe2b2d35c1468be9dcde87d51ed5a316d04697f39d8297aab81b6c0aa09aa318c377df5b1da8a097f10170dd7bcc1c216535dc8f56236df935ab689f614e19e029b34e49edb987f3bc63c28f62fe042a5680f4043aec961757f8e6cb8337dcf835f9a53cdc7ca096566fe34dd09e0933d49b7428efc196f8c2cdaa061d7ca430fdedac782df66fabfb8e11b2ce258bbe92cd914a5b8af5ceae1f6b5afbc376cbb685da8afffc8476be61babd6f36226b56836ed700f008a83e7a0cda0ed620132ce2cb3fba7f8cd6d925395eb38a4801b1962327facecf467b393455bc61374783c9dd1b800e6f504339520eb87d931c9fe1b5fd67d7ca32f5e1c881fad1ca8c4d3ce3994ba1c7c257c2b64c59375ea96f76a0e62a2e4d54d5912fa9674d72ba3066f238a42276b9ce7c2ba481fd4836b8a5a7b60cd5092875d9e036bded7b47ce8778248c5a197c61a9c4a54e5b29deead4ebe3768467473d3906ee86d9e04e459a5bd0905e4e39d05e697af6e30b212a340e1acaccbf5004ffb6e54def3d5efb722b251d65aad4840473f74e41f8b6bdda42e988efecbee5b2edf735de4f7ad8f86ecde9c9bddae8cba88d69a8bf82cef010cc8802a515036fd0970805532b1af067a5a7e13543d3a7454281ce8ffd7b7daf5e75baead4e289c8b49a6a83a3bc6a959635b920c47f3de29afa47022d1a8edac4aaa397578ba386b80cadb3d4f8a19e0ec8bfe9a9bc3116c2c0dc1138c726c103bfab94d3a5e74e1abcffd2e0c54a4d1548ef250f1cc78ab508455aedd9dd4172597392749e39dd829a777b8c26d06aef2525da51d329f51565a1749c95d571df17e68ee3d9440087706a286be1899a11f092a23fc992f01ffa114cd4f1de3eeb47c17646f62a592cd4e8894a011696db564a07b082451d7777ba23d51fe6dd007fdf1b88f05e3c8a99bd2ea8dc3018b10ced6a6a25a5548fc419af5c7d82a8e4dee8fbdb097af1eaaa3f21fa7ce6d5b2883481ae9cb7c783913bd21c32aa6ac0f33d28d342d024adb5fdce7b8e34db103c50e6337e3c4b70927680370fdd47a50923f95b22add69635188d24fce3503610aec4eb786ebbb4380e69a2d79922c901582b044c35316dd45987c6e009a43bfc096d3003fc93ba6027378b190a6c9f7fffe381a713cf3eb86c6d9bd001108d3687e6b7ffc15e86d4e337131649f087abea317947971a5bbfff6ce2e4a4be89e602a52751db658893a23c9b169fca7a626547667aa6bbf2e88e54b7d95cc21e0c84dd41a1fe8564b60c8d7d575baf7f10d8b10ab3d4c911b3c9d48c78202942cd39f444962795f6fd97bc815a4c686f5ee3faf49e9c0a41549738ee928644c559dd97271acada7937b96457df9083b41c50c1b2761d4e172c1a9479419a7150092669d41979baa945dda0fbaa976bd58250124721e38a0d10daf1bc9bbf60441e2c01e09027cbd63051e09ccd2393f3b60a8a8013939cbaefc68ac65388f553e30d7662c196085d470dace85d796523e4f966d86f2c890919b754fa490a61c23c9e98eb3dd7463110e3c69d9813ab9f2faec3f53848fab2b5183a35f43c80a1d18a3a5323a51a1634f98dc3fa3b893f3083c56d55b4b0e0920d358b591141ca44fe8a09bdade51103818b7c4859fe1a315bc53893a432c5238face1543df00f733ada2e77aaac6a214c8ad8bb5059d96c1c27295660c0aea406df059b5a1543cff44e499f00127ebf086262302bd979d853543c15e4be755f4873d58743e3e60c73a85d9efdde9e57c5bede2a9a1945af36f645a4f251937e50a7e60c0139c44a8cacc928179309aa82f06d84a880df081e45ef69ec880093073864accedd06952c46e7ae880f6dc2bfa45e4893fd968d661fd2e6e8637ad891529173272f66081e9556e6bc1997b687255cd27deeff8e509e1851e046b8c4463e4da4251c34183f8557bf0854ebc7557ba96982896c562d4ac99b0b88a574a3fa20e5ca0c1d0ed1683009310a68fe92569a9f6d75c2162ed5d0bae81d01bed176b711cef";
        long begin = System.currentTimeMillis();
        byte[] compressed = compress(input);
        print(compressed);
        long end = System.currentTimeMillis();
        printTime(begin, end);
        br();
        String uncompressed = decompressToString(compressed, calcLength(input.length()));
        printTime(end);
        String result = new String(uncompressed);
        print(result);
    }
}