package util;

import java.util.Scanner;

import static util.DESCrypt.crypt_mode.DECRYPT_MODE;
import static util.DESCrypt.crypt_mode.ENCRYPT_MODE;

/**
 * 测试一下加密解密是否成功，测试结果良好
 * @author Mika
 * @since 2021-1-14 12:19:56
 */

public class DesTest {
    public static void main(String[] args){
        int mode;
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("输入首位数字选择模式：\n1. DES加密\n2. DES解密\n0. 退出");
            mode = sc.nextInt();
            sc.nextLine();
            System.out.println("----------------------------------------------");
            if(mode==1){
                String cipher = new String();
                String key = new String();
                String result = new String();
                System.out.print("请输入明文：");
                cipher = sc.nextLine();
                System.out.print("请输入秘钥：");
                key = sc.nextLine();

                DESCrypt desCrypt = new DESCrypt();
                result = desCrypt.doFinal(ENCRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
                System.out.println("加密密文："+result);
            }else if(mode ==2){
                String cipher = new String();
                String key = new String();
                String result = new String();
                System.out.print("请输入密文：");
                cipher = sc.nextLine();
                System.out.print("请输入秘钥：");
                key = sc.nextLine();

                DESCrypt desCrypt = new DESCrypt();
                result = desCrypt.doFinal(DECRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
                System.out.println("解密明文："+result);
            }
            else if(mode == 0){
                break;
            }
        }
    }
}
