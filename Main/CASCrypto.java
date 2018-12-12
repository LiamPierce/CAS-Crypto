/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Crypto.ElGamal;
import static Crypto.ElGamal.Algorithm.babyStepGiantStep;
import static Crypto.ElGamal.Algorithm.indexCalculus;
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

    /**
     * Test of pseduorandom generator, RSA, and El Gamal.
     * 
     * @throws Exception
     */
    public static void test() throws Exception{
        SecurePseudoGenerator gen = new SecurePseudoGenerator();
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
            if (!rsaTestObject.decrypt(rsaTestObject.encrypt(testMessage)).equals(testMessage)){
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
    
    /**
     *
     * @param args Command line arguments.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CASCrypto.test();
        //Commented out below are many of the functions I used during the exchange.
        
        /*
        RSA rsaTestObject = new RSA(120);
        keyPost k = rsaTestObject.post();
        System.out.println(rsaTestObject.encrypt(BigInteger.valueOf(200)));
        */
        
        /*BigInteger inverse = Algorithms.cyclicInverse(new BigInteger("12591839056446721"), new BigInteger("60759056899137564"));
        System.out.println("inverse : " + inverse.mod(new BigInteger("60759056899137564")));*/
        
        //System.out.println(Algorithms.fastExponentiate(inverse, BigInteger, BigInteger.ONE))
        /*
        RSA rsaInitObject = new RSA(new BigInteger("1629269071"),new BigInteger("2147483647"),new BigInteger("69216523"));
        System.out.println(rsaInitObject.decrypt(new BigInteger("2723350590626791597")));
        */
        
        //Actions
        /*
        RSA rsaObject = new RSA(new BigInteger("60759057844448749"),new BigInteger("12591839056446721"));
        System.out.println("Encrypted : " + rsaObject.encrypt(new BigInteger("27899")));
        
        System.out.println("Recrypt: " + RSA.maliciousCrypt(new BigInteger("77786329"),new BigInteger("8971")).decrypt(new BigInteger("71123057")));
        */
        /*
        //Encrypt with external
        RSA rsaTestObject = new RSA(new BigInteger("9419"),new BigInteger("8647"),new BigInteger("19"));
        keyPost k = rsaTestObject.post();
        System.out.println("Encrypted : " + rsaTestObject.decrypt(BigInteger.valueOf(4372)));
        
        */
        
        /*ElGamal s = new ElGamal(11);
        s.post();*/
        
        //System.out.println(Algorithms.pollardsPMinusOne(new BigInteger("3801911"), BigInteger.ONE));
        /*
        ElGamal k = new ElGamal(new BigInteger("158366981219"), new BigInteger("259701736"),new BigInteger("243030405435"));
        System.out.println("Decrypted public : " + k.decrypt(new BigInteger("48102023024"), new BigInteger("128699235079")));
        
        RSA z = RSA.maliciousCrypt(new BigInteger("26369440358836820992378760775624580400033667088791832087692367883383117823222429688047447566837491613684887402380397001307150901320138560960715462328134927686214708771440272055008296592553428132342595505530407117423612799606238748521374226473794434644727512892219818163587617010455445363216319354349866945542037382791865219415894506057818182733643450631665921291903450221051475626439382143346347096296456440177159635452276111098745906414883693415061417660936253103237980115716613546987279657260348942260935963195539502913969910708281541080790555774423920672358772815502545893766268420683977834367869863896062806107733"), new BigInteger("65537"));
        z.post();
        /*
        System.out.println("Decrypt: " + ElGamal.maliciousDecrypt(babyStepGiantStep, new BigInteger("10875685270"), new BigInteger("146661069251"), new BigInteger("432943"), new BigInteger("3983221626"),new BigInteger("349168709")));
        ElGamal m = new ElGamal(11);
        m.post();
        
        System.out.println("Encrypted : " + encrypted);
        System.out.println("Decrypted : " + k.decrypt(new BigInteger("110082702"), encrypted));
        System.out.println("Malicious decrypt :" + ElGamal.maliciousDecrypt(babyStepGiantStep, new BigInteger("272621523"), new BigInteger("354263099"), new BigInteger("11983"), new BigInteger("62662996"), new BigInteger("110082702")));
       */
        
        ElGamal m = new ElGamal(new BigInteger("3173696634"),new BigInteger("29717"));
        System.out.println(m.encrypt(new BigInteger("1201089503"), BigInteger.valueOf(4321)));
        
       
        //System.out.println(ElGamal.maliciousDecrypt(babyStepGiantStep, new BigInteger("7715180"), new BigInteger("10111747"), new BigInteger("3821"), new BigInteger("1400009"), new BigInteger("4090346")));
        
        
    }
    
}
