package Coding;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.Scanner;

/**
 * Created by Timkabor on 11/17/2017.
 */
public class CyclicRedundancyCheck implements Encoder,Decoder {
    int[] crc;
    int[] rem;
    int[] divisor = {1,1,0,1,0,1};

    @Override
    public byte[] encode(byte[] message) {
        int[] data;
        int[] div;
        int data_bits, divisor_bits, tot_length;

        BitSet bitset = BitSet.valueOf(message);
        data=new int[bitset.size()];

        for(int i=0; i<bitset.size(); i++) {
            data[i] = bitset.get(i) ? 1 : 0;
            System.out.print(data[i]);
        }
        System.out.println();
       tot_length=bitset.size()+divisor.length-1;

        div=new int[tot_length];
        rem=new int[tot_length];
        crc=new int[tot_length];
    /*------------------ CRC GENERATION-----------------------*/
        for(int i=0;i<data.length;i++)
            div[i]=data[i];

        System.out.print("Dividend (after appending 0's) are : ");
        for(int i=0; i< div.length; i++)
            System.out.print(div[i]);
        System.out.println();

        for(int j=0; j<div.length; j++){
            rem[j] = div[j];
        }

        rem=divide(div, divisor, rem);

        for(int i=0;i<div.length;i++)           //append dividend and ramainder
        {
            crc[i]=(div[i]^rem[i]);
        }

        System.out.println();
        System.out.println("CRC code : ");
        for(int i=0;i<crc.length;i++)
            System.out.print(crc[i]);
        return encodeToByteArray(crc);
    }

    static int[] divide(int div[],int divisor[], int rem[])
    {
        int cur=0;
        while(true)
        {
            for(int i=0;i<divisor.length;i++)
                rem[cur+i]=(rem[cur+i]^divisor[i]);

            while(rem[cur]==0 && cur!=rem.length-1)
                cur++;
            System.out.println(rem.length-cur + " " + divisor.length);
            if((rem.length-cur)<divisor.length)
                break;
        }
        return rem;
    }
    private static byte[] encodeToByteArray(int[] bits) {
        BitSet bitSet = new BitSet(bits.length);
        for (int index = 0; index < bits.length; index++) {
            bitSet.set(index, bits[index] > 0);
        }
        return bitSet.toByteArray();
    }
    public byte[] decode(byte[] message)
    {
         /*-------------------ERROR DETECTION---------------------*/
        System.out.println();
        BitSet bitset = BitSet.valueOf(message);

        for(int i=0; i<crc.length; i++)
            crc[i] = bitset.get(i) ?  1 : 0;


/*        System.out.print("crc bits are : ");
        for(int i=0; i< crc.length; i++)
            System.out.print(crc[i]);
        System.out.println();
*/
        for(int j=0; j<crc.length; j++){
            rem[j] = crc[j];
        }

        rem=divide(crc, divisor, rem);

        for(int i=0; i< rem.length; i++)
        {
            if(rem[i]!=0)
            {
                System.out.println("Error");
                return null;
            }
            if(i==rem.length-1)
                System.out.println("No Error");
        }

        System.out.println("THANK YOU.... :)");
        return message;
    }
}
