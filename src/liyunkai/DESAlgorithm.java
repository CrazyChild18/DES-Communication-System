package liyunkai;

public class DESAlgorithm {
    
	byte[] key;
    
	// 声明常量字节数组
    public DESAlgorithm(String key) {
        this.key = key.getBytes();
    }
    
    private static Data data_parameter = new Data();
    private static final int[] IP = data_parameter.getIP();
    // 64 逆初始置换表
    private static final int[] IP1 = data_parameter.getIP1();
    private static final int[] PC1 = data_parameter.getPC1();
    // 循环移位表、用于子密钥的左移
    private static final int[] LeftMove = data_parameter.getLeftMove();
    // 48
    private static final int[] PC2 = data_parameter.getPC2(); 
    // 48
    private static final int[] E = data_parameter.getE(); 
    private static final int[] P = data_parameter.getP();
    //S盒，8个S变换表。
    private static final int[][][] SBox = data_parameter.getSBox();
    
    /** 
     * 加解密的控制流程操作
     * 核心操作
     * @param inputData: 传入输入内容的byte
     * 		加密时传入data.getBytes
     * 		解密时使用数据的byte类型的密文
     * @param flag: 表示加密或解密操作
     */
    public byte[] DesStart(byte[] inputData, int flag) {
    	// 首先将byte数据每8位拆分
    	// 通过格式化保证数据长度为8的整数倍
        byte[] fill_key = FillByteData(key);
        byte[] fill_data = FillByteData(inputData);
        int dataLen = fill_data.length;
        byte[] result_data = new byte[dataLen];
        // 计算数据长度可划分为多少8位
        int multiple = dataLen / 8;
        
        // 加密 or 解密运算
        // 分组成8个字节一组进行加密，最后再合并
        // 得到result_data为总结果
        for (int i = 0; i < multiple; i++) {
            byte[] tmpkey = new byte[8];
            byte[] tmpdata = new byte[8];
            System.arraycopy(fill_key, 0, tmpkey, 0, 8);
            System.arraycopy(fill_data, i * 8, tmpdata, 0, 8);
            byte[] tmpresult = CodedData(tmpkey, tmpdata, flag);
            System.arraycopy(tmpresult, 0, result_data, i * 8, 8);
        }
        
        // 若为解密则去掉填充位
        byte[] decrypData = null;
        if (flag == 0) {
        	System.out.print("After: ");
        	for(byte e : result_data) {
        		System.out.print(e + " ");
        	}
        	System.out.println();
        	// 获得包含补位的总长度
            int total_len = dataLen; //16
            int delete_len = result_data[total_len - 8 - 1]; 
            if ((delete_len >= 1) && (delete_len <= 8)) {
            	
            }else {
            	delete_len = 0;
            }
            decrypData = new byte[total_len - delete_len - 8];
            boolean del_flag = true;
            for (int k = 0; k < delete_len; k++) {
                if (delete_len != result_data[total_len - 8 - (k + 1)])
                    del_flag = false;
            }
            if (del_flag == true) {
                System.arraycopy(result_data, 0, decrypData, 0, total_len - delete_len - 8);
            }
        }
        
        // 用来输出密文和解密后明文的ascii码组
        // flag为1时输出密文ascii码组, 为0时输出解密后明文ascii码组
        String bitData = "";
        if (flag == 1){
            for (int i = 0; i < result_data.length; i++) {
            	bitData += String.valueOf(result_data[i]);
            }
            String regEx = "[-]";
            String a = "";
            bitData = bitData.replaceAll(regEx, a);
        }else if (flag == 0){
            for (int i = 0; i < decrypData.length; i++) {
            	bitData += String.valueOf(decrypData[i]);
            }
        }
        if(flag == 1) {
        	return result_data;
        }else {
        	return decrypData;
        }
    }
    
    /**
     * 编码数据
     * 将输入des_data转化为二进制字节数组
     * 将输入des_key转化为二进制字节数组
     * @param des_key 处理长度后密钥
     * @param des_data 处理长度后数据
     * @param flag 加密或解密
     * @return encrypt 最终运算结果
     */
    private byte[] CodedData(byte[] des_key, byte[] des_data, int flag) {
    	// 将密钥字节数组转换成二进制字节数组
        int[] keydata = new int[64];
        keydata = ByteToBirnary(des_key);
        
        // 将加密数据字节数组转换成二进制字节数组
        int[] encryptdata = new int[64];
        encryptdata = ByteToBirnary(des_data);
        
        // 生成子密钥
        int[][] sub_key = GetSubKey(keydata);
        
        // 执行加密解密操作
        int i;
        int flags = flag;
        //存放初始变换后的64位数据
        int[] M = new int[64];
        //存放逆初始变换后的64位数据
        int[] MIP_1 = new int[64];
        
        for (i = 0; i < 64; i++) {
        	// 明文IP初始变换，物理索引=逻辑索引-1
            M[i] = encryptdata[IP[i] - 1];
        }
        
        if (flags == 1) { // 加密
            for (i = 0; i < 16; i++) {
            	fFunction(M, i, flags, sub_key);
            }
        } else if (flags == 0) { // 解密
            for (i = 15; i > -1; i--) {
            	fFunction(M, i, flags, sub_key);
            }
        }

        for (i = 0; i < 64; i++) {
        	// 进行逆初始置换IP-1运算
            MIP_1[i] = M[IP1[i] - 1];
        }
        // 将64位数据转换为8位字节
        byte[] encrypt = BirnaryToByte(MIP_1);
        //返回操作完成后字节数组
        return encrypt;
    }
    
    /**
     * 生成子秘钥
     * @param keyarray: 存储每一次生成的密钥
     * @param key: 传入总密钥
     * @return 
     */
    private int[][] GetSubKey(int[] key) {
    	int[][] sub_key = new int[16][48];
        int i, j;
        int[] K0 = new int[56];
        for (i = 0; i < 56; i++) {
        	// 密钥进行PC-1变换
            K0[i] = key[PC1[i] - 1];
        }
        for (i = 0; i < 16; i++) {
        	//按轮次对子密钥进行数据左移
        	LeftMove(K0, LeftMove[i]);
            for (j = 0; j < 48; j++) {
            	//进行PC2变换生成生成子密钥keyarray[i][j]
            	sub_key[i][j] = K0[PC2[j] - 1];
            }
        }
        return sub_key;
    }
    
    /**
     * 子密钥左移，根据循环移位表
     * @param k: 总密钥
     * @param offset: 偏移数
     */
    private void LeftMove(int[] k, int offset) {
        int i;
        int[] c0 = new int[28];
        int[] d0 = new int[28];
        int[] c1 = new int[28];
        int[] d1 = new int[28];
        //将56位数据平分成两组28位cO和cO
        for (i = 0; i < 28; i++) {
            c0[i] = k[i];
            d0[i] = k[i + 28];
        }
        if (offset == 1) {
        	//循环左移一位
            for (i = 0; i < 27; i++) {
                c1[i] = c0[i + 1];
                d1[i] = d0[i + 1];
            }
            c1[27] = c0[0];
            d1[27] = d0[0];
        } else if (offset == 2) {
        	//循环左移两位
            for (i = 0; i < 26; i++) {
                c1[i] = c0[i + 2];
                d1[i] = d0[i + 2];
            }
            c1[26] = c0[0];
            d1[26] = d0[0];
            c1[27] = c0[1];
            d1[27] = d0[1];
        }
        //将做完数据左移后的左右两组28位数据c1和d1合并
        for (i = 0; i < 28; i++) {
            k[i] = c1[i];
            k[i + 28] = d1[i];
        }
    }
    
    /**
     * 将数据转换为二进制数
     * 存储到字节数组
     */
    private int[] ByteToBirnary(byte[] intdata) {
        int i;
        int j;
        int[] IntDa = new int[8];
        for (i = 0; i < 8; i++) {
            IntDa[i] = intdata[i];
            if (IntDa[i] < 0) {
                IntDa[i] += 256;
                IntDa[i] %= 256;
            }
        }
        int[] IntVa = new int[64];
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                IntVa[((i * 8) + 7) - j] = IntDa[i] % 2;
                IntDa[i] = IntDa[i] / 2;
            }
        }
        return IntVa;
    }
    
    /** 
     * E扩充
     * @param R0
     * @param keyarray
     * @param times
     * @return RE 存放经过E变换后的数据
     * 
     * 经过E变换扩充，由32位变为48位
     * 		首先经过E变换后的数据与子密钥KeyArray[times][i]按位作不进位加法运算
     * 		然后异或运算
     */
    private int[] E(int[] R0, int[][] keyarray, int times) {
    	int[] RE = new int[48];
        for (int i = 0; i < 48; i++) {
            RE[i] = R0[E[i] - 1];
            RE[i] = RE[i] + keyarray[times][i];
            //异或规则，当两个相等的数异或时结果为0时，它们的算术和为2或0
            if (RE[i] == 2 || RE[i] == 0) {
                RE[i] = 0;
            }
        }
    	return RE;
    }
    
    /**
     *  S盒变换
     *  将48位数据分成8组，每组6位
     *  通过运算转化为6bit的数据
     *  重新组合
     * @param RE
     * @return ans
     */
    private int[] S(int[] input) {
    	int[][] S = new int[8][6];
    	int[] sBoxData = new int[8];
    	int[] output = new int[32];
    	for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                S[i][j] = input[(i * 6) + j];
            }
            // 下面经过S盒，得到8个数
            sBoxData[i] = SBox[i][(S[i][0] << 1) + S[i][5]][(S[i][1] << 3) + (S[i][2] << 2) + (S[i][3] << 1) + S[i][4]];
            // 8个数变换输出为二进制
            for (int j = 0; j < 4; j++) {
            	output[((i * 4) + 3) - j] = sBoxData[i] % 2;
                sBoxData[i] = sBoxData[i] / 2;
            }
        }
    	return output;
    }
    
    /**
     *  P变换
     *  将S盒输出的32位数据作为参数输入进行P变换，P[]是循环移位表
     *  	RP[i]存放根据循环变换表将输入数据的第n位
     *  	异或操作
     *  	将左右32位数据组L和R重新合成M，返回数组M
     *  	最后一次变换时，左右不进行互换。此处采用两次变换实现不变
     * @param S S盒运算输出
     * @param M 传入内容
     * @param L0
     * @param L1
     * @param R0
     * @param R1
     * @param flag
     * @param times
     */
    private void P(int[] S, int[] M, int[] L0, int[] L1, int[] R0, int[] R1, int flag, int times) {
    	int[] RP = new int[32];
    	for (int i = 0; i < 32; i++) {
    		// RP[i]存放根据循环变换表将输入数据的第n位
    		RP[i] = S[P[i] - 1];
            // 右边移到左边
            L1[i] = R0[i];
            // 异或运算
            R1[i] = L0[i] + RP[i];
            if (R1[i] == 2 || R1[i] == 0) {
                R1[i] = 0;
            }
            // 将左右32位数据组L和R重新合成M，返回数组M
            if (((flag == 0) && (times == 0)) || ((flag == 1) && (times == 15))) {
                M[i] = R1[i];
                M[i + 32] = L1[i];
            }
            else {
                M[i] = L1[i];
                M[i + 32] = R1[i];
            }
        }
	}
    
    /***f函数，包括E变换、与子密钥异或、S盒压缩、P变换***/
    private void fFunction(int[] M, int times, int flag, int[][] keyarray) {
        int[] L0 = new int[32];
        int[] R0 = new int[32];
        int[] L1 = new int[32];
        int[] R1 = new int[32];
        
        //将64位明文分为左右32位的两组数据
        for (int i = 0; i < 32; i++) {
        	// 明文左侧的初始化
            L0[i] = M[i];
            // 明文右侧的初始化
            R0[i] = M[i + 32];
        }
        // E变换扩充
        int[] RE = E(R0, keyarray, times);
        //S盒变换
        int[] sValue = S(RE);
        // P变换
        P(sValue, M, L0, L1, R0, R1, flag, times);
    }
    
    /***将存储64位二进制数据的数组中的数据转换为八个整数（byte）****/
    private byte[] BirnaryToByte(int[] data) {
    	byte[] value = new byte[8];
        int i, j;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                value[i] += (data[(i << 3) + j] << (7 - j));
            }
        }
        for (i = 0; i < 8; i++) {
            value[i] %= 256;
            if (value[i] > 128) {
                value[i] -= 255;
            }
        }
        return value;
    }
    
    /**
     * 格式化，不足8位倍数字节的补充
     * 算出缺多少位到8的整数倍
     * 补充位数使用缺失数量的byte
     */
    private byte[] FillByteData(byte[] data) {
        int len = data.length;
        int need = 8 - (len % 8);
        byte[] fillData = new byte[len + need];
        System.arraycopy(data, 0, fillData, 0, len);
        for (int i = len; i < len + need; i++)
        	fillData[i] = (byte) need;
        System.out.print("Before: ");
        for (byte e : fillData) {
        	System.out.print(e + " ");
        }
        System.out.println();
        return fillData;
    }

    
}