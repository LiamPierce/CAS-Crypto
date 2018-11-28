/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Crypto.ElGamal;
import Crypto.PrimeGenerator;
import Crypto.RSA;
import Crypto.RSA.keyPost;
import Crypto.SecurePseudoGenerator;
import Crypto.SecurePseudoGenerator.Generator;
import static Crypto.SecurePseudoGenerator.Generator.BlumBlumShub;
import L1.Algorithms;
import static java.lang.System.exit;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Liam Pierce
 */
public class CASCrypto {
/*
    /**
     * @param args the command line arguments
     */
    public static void test() throws Exception{
        SecurePseudoGenerator gen = new SecurePseudoGenerator(BlumBlumShub);
        HashMap<BigInteger,Integer> randoms = new HashMap<>();
        int tests = 200;
        for (int i = 0;i<tests;i++){
            BigInteger random = gen.generateNumber(10);
            randoms.put(random,randoms.containsKey(random) ? randoms.get(random) + 1 : 1);
            
        }
        
        int score = 0;
        for (BigInteger random : randoms.keySet()){
            score += randoms.get(random);
        }
        if ((double) score / (double) tests < 1.2){
            System.out.println("Random Generator acceptable. Score : " + (double) score / (double) tests);
        }else{
            System.out.println("!!Random Generator unacceptable!!");
        }
        
        boolean rsaFunctional = true;
        for (int rsaTests=0;rsaTests<20;rsaTests++){
            System.out.println("Test " + (rsaTests + 1));
            RSA rsaTestObject = new RSA(5);
            //keyPost k = m.post();
            BigInteger testMessage = BigInteger.valueOf(100);
            if (!rsaTestObject.localAction(rsaTestObject.publicAction(testMessage)).equals(testMessage)){
                System.out.println("Test failed.");
                rsaFunctional = false;
                exit(1);
                break;
            }
        }
        if (rsaFunctional){
            System.out.println("Test succeeded");
        }
        
        boolean elGamalFunctional = true;
        for (int egTests=0;egTests<20;egTests++){
            System.out.println("Test " + (egTests + 1));
            ElGamal a = new ElGamal(10);
            ElGamal b = new ElGamal(a.group,a.generator);
            
            //keyPost k = m.post();
            BigInteger testMessage = BigInteger.valueOf(100);
            if (!a.decrypt(b.bl,b.encrypt(a.bl,testMessage)).equals(testMessage)){
                System.out.println("Test failed.");
                elGamalFunctional = false;
                exit(1);
                break;
            }
        }
        if (rsaFunctional){
            System.out.println("Test succeeded");
        }
    }
    public static void main(String[] args) throws Exception {
        
        CASCrypto.test();
        
        
        /*
        //Testing modules.
        //RSA can be done with the same object.
        RSA m = new RSA(10);
        keyPost k = m.post();
        System.out.println(m.localAction(m.publicAction(BigInteger.valueOf(100))));
        System.out.println("EVE : " + RSA.maliciousCrypt(k.n,k.e).localAction(m.publicAction(BigInteger.valueOf(100))));
        System.out.println(m.localAction(m.publicAction(BigInteger.valueOf(100))));

        */
        //El Gamal needs 2 because of its design.
        /*
        ElGamal a = new ElGamal(10);
        ElGamal b = new ElGamal(a.group,a.generator);
        a.post();
        System.out.println("Message : 100, b^r : " + a.bl + " b^l : " + b.bl);
        
        BigInteger ciphertext = a.encrypt(b.bl, BigInteger.valueOf(43));
        System.out.println("eve message: " + ElGamal.maliciousDecrypt(ciphertext, a.group, a.generator, a.bl,b.bl));
        System.out.println("eve ecrypted: " + ElGamal.maliciousEncrypt(BigInteger.valueOf(43), a.group, a.generator, a.bl,b.bl));
        System.out.println(ciphertext + " encrypted.");
        System.out.println(b.decrypt(a.bl, ciphertext));
        //System.out.println(Algorithms.primeFactorization(BigInteger.valueOf(2287)).keySet().size());
        */
        //System.out.println("log base 2 (3) in zx29 : " + Algorithms.babyStepGiantStep(BigInteger.valueOf(29), BigInteger.valueOf(2), BigInteger.valueOf(3)));
        /*
       
*/
        
        
        
    }
    
}
