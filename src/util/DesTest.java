package util;

import java.util.Scanner;

import static util.DESCrypt.crypt_mode.DECRYPT_MODE;
import static util.DESCrypt.crypt_mode.ENCRYPT_MODE;

/**
 * ����һ�¼��ܽ����Ƿ�ɹ������Խ������
 * @author Mika
 * @since 2021-1-14 12:19:56
 */

public class DesTest {
    public static void main(String[] args){
        int mode;
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("������λ����ѡ��ģʽ��\n1. DES����\n2. DES����\n0. �˳�");
            mode = sc.nextInt();
            sc.nextLine();
            System.out.println("----------------------------------------------");
            if(mode==1){
                String cipher = new String();
                String key = new String();
                String result = new String();
                System.out.print("���������ģ�");
                cipher = sc.nextLine();
                System.out.print("��������Կ��");
                key = sc.nextLine();

                DESCrypt desCrypt = new DESCrypt();
                result = desCrypt.doFinal(ENCRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
                System.out.println("�������ģ�"+result);
            }else if(mode ==2){
                String cipher = new String();
                String key = new String();
                String result = new String();
                System.out.print("���������ģ�");
                cipher = sc.nextLine();
                System.out.print("��������Կ��");
                key = sc.nextLine();

                DESCrypt desCrypt = new DESCrypt();
                result = desCrypt.doFinal(DECRYPT_MODE,cipher,key, DESCrypt.paraCoding.NOBASE64);
                System.out.println("�������ģ�"+result);
            }
            else if(mode == 0){
                break;
            }
        }
    }
}
