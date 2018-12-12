/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import static Crypto.ElGamal.Algorithm.babyStepGiantStep;
import static Crypto.SecurePseudoGenerator.Generator.BlumBlumShub;
import L1.Algorithms;
import static L1.Algorithms.cyclicInverse;
import static L1.Algorithms.fastExponentiate;
import java.math.BigInteger;

/**
 *
 * @author Liam Pierce
 */
public class ElGamal {
    private SecurePseudoGenerator pRNG = new SecurePseudoGenerator(BlumBlumShub);
    private PrimeGenerator pgen = new PrimeGenerator();
    
    public BigInteger group;

    public BigInteger generator;
    private BigInteger r; 

    public BigInteger bl;
    
    /**
        Initializes a new El Gamal client with a given group and generator.
    
        @param group the preset group of the client.
        @param generator the preset generator of the client.
    */

    public ElGamal(BigInteger group,BigInteger generator){
        this.group = group;
        this.generator = generator;
        this.r = pRNG.generateNumber(group.toString().length());
        this.bl = fastExponentiate(this.generator,r,group);
        System.out.println("Initiated El Gamal with group : " + group + " generator : " + generator + " r: " + r + " b^r: " + bl); 
    }
    
    /**
        Initializes a new El Gamal client with a precreated group, generator, and r.
    
        @param group the preset group of the client.
        @param generator the preset generator of the client.
        @param r the preset r of the client.
    */
    public ElGamal(BigInteger group, BigInteger generator, BigInteger r){
        this.group = group;
        this.generator = generator;
        this.r = r;
        this.bl = fastExponentiate(this.generator,r,group);
        System.out.println("Initiated El Gamal from ggr with group : " + group + " generator : " + generator + " r: " + r + " b^r: " + bl); 
    }
    
    /**
        Initializes an El Gamal client with a group of [digits] digits.
    
        @param digits The number of digits for the group.
     * @throws java.lang.Exception
    */
    public ElGamal(int digits) throws Exception{
        this.group = pgen.generatePrime(pRNG, digits); // Group needs to be a prime to have generators.
        System.out.println("Starting El Gamal cipher. Group : " + group);
        this.generator = Algorithms.primitiveRootSearch(pgen, group);
        this.r = pRNG.generateNumber(digits);
        this.bl = fastExponentiate(this.generator,r,group);
        System.out.println("Initiated El Gamal from digits with group : " + group + " generator : " + generator + " r: " + r + " b^r: " + bl); 
    }
    
    /**
        Decryption algorithm.
    */
    public enum Algorithm{

        /**
         *
         */
        babyStepGiantStep,

        /**
         *
         */
        indexCalculus;
    }
    
    /**
        Eavesdropper's decryption.
    
       @param dlB the algorithm to use to solve the discrete log problem.
       @param ciphertext the ciphertext to decrypt.
       @param group the group of the ciphertext's El Gamal.
       @param b the generator of the ciphertext's El Gamal.
       @param bl the secret of the first El Gamal participant.
       @param br the secret of the second El Gamal participant.
       @return plaintext.
    */
    public static BigInteger maliciousDecrypt(Algorithm dlB,BigInteger ciphertext,BigInteger group,BigInteger b,BigInteger bl,BigInteger br){
        BigInteger l = (dlB == babyStepGiantStep) ? Algorithms.babyStepGiantStep(group, b, bl) : Algorithms.indexCalculus(group, b,bl);
        System.out.println("b^rl : " + fastExponentiate(br,l,group));
        System.out.println("b^-rl : " + cyclicInverse(fastExponentiate(br,l,group),group).mod(group));
        return ciphertext.multiply(cyclicInverse(fastExponentiate(br,l,group),group).mod(group)).mod(group);
    }
    
    /**
        Eavesdropper's encryption.
    
        @param dlB the algorithm to use to solve the discrete log problem.
        @param message the ciphertext to decrypt.
        @param group the group of the ciphertext's El Gamal.
        @param b the generator of the ciphertext's El Gamal.
        @param bl the secret of the first El Gamal participant.
        @param br the secret of the second El Gamal participant.
        * @return 
    */
    public static BigInteger maliciousEncrypt(Algorithm dlB,BigInteger message,BigInteger group,BigInteger b,BigInteger bl,BigInteger br){
        BigInteger l = (dlB == babyStepGiantStep) ? Algorithms.babyStepGiantStep(group, b, bl) : Algorithms.indexCalculus(group, b,bl); //b^r
        return message.multiply(fastExponentiate(br,l,group)).mod(group);
    }
    
    /**
        Client's decryption method.
    
        @param bl the public b^l of the encrypting client.
        @param ciphertext the ciphertext to decrypt.
        @return plaintext.
    */
    public BigInteger decrypt(BigInteger bl,BigInteger ciphertext){
        return cyclicInverse(fastExponentiate(bl,r,group),group).mod(group).multiply(ciphertext).mod(group);
    }
    
    /**
        Client's encryption method.
    
        @param bl the public b^l of the decrypting client.
        @param message the ciphertext to decrypt.
        @return ciphertext
    */
    public BigInteger encrypt(BigInteger bl,BigInteger message){
        //System.out.println("Group : " + group);
        return fastExponentiate(bl,r,group).multiply(message).mod(group);
        
    }
    
    /**
        Publish the public data of this El Gamal client.
    */
    public void post(){
        System.out.println("b   : " + this.generator);
        System.out.println("b^r : " + bl);
    }
}
