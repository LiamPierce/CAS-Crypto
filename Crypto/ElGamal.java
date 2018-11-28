/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

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
    public BigInteger r; //****
    public BigInteger bl; //For public use. "bl" signifies b^l, what the recipient of a message would see as this suite's generator ^ r.
    
    public ElGamal(BigInteger group,BigInteger generator){
        this.group = group;
        this.generator = generator;
        this.r = pRNG.generateNumber(group.toString().length());
        this.bl = fastExponentiate(this.generator,r,group);
    }
    
    public ElGamal(int digits) throws Exception{
        this.group = pgen.generatePrime(pRNG, digits); // Group needs to be a prime to have generators.
        System.out.println("Starting El Gamal cipher. Group : " + group);
        this.generator = Algorithms.primativeRootSearch(pgen, group);
        this.r = pRNG.generateNumber(digits);
        this.bl = fastExponentiate(this.generator,r,group);
    }
    
    public static BigInteger maliciousDecrypt(BigInteger ciphertext,BigInteger group,BigInteger b,BigInteger bl,BigInteger br){
        BigInteger l = Algorithms.babyStepGiantStep(group, b, bl); //b^r
        return ciphertext.multiply(cyclicInverse(fastExponentiate(br,l,group),group).mod(group)).mod(group);
    }
    
    public static BigInteger maliciousEncrypt(BigInteger message,BigInteger group,BigInteger b,BigInteger bl,BigInteger br){
        BigInteger l = Algorithms.babyStepGiantStep(group, b, bl); //b^r
        return message.multiply(fastExponentiate(br,l,group)).mod(group);
    }
    
    public BigInteger decrypt(BigInteger bl,BigInteger ciphertext){
        return cyclicInverse(fastExponentiate(bl,r,group),group).mod(group).multiply(ciphertext).mod(group);
    }
    
    public BigInteger encrypt(BigInteger bl,BigInteger message){
        //System.out.println("Group : " + group);
        return fastExponentiate(bl,r,group).multiply(message).mod(group);
        
    }
    
    public void post(){
        System.out.println("b   : " + this.generator);
        System.out.println("b^r : " + bl);
    }
}
