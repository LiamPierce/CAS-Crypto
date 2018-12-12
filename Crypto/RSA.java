/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import static Crypto.SecurePseudoGenerator.Generator.BlumBlumShub;
import L1.Algorithms;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @author Liam Pierce
 */
public class RSA {

    /**
    * Class representing a key's publishing.
    */
    public class keyPost{

        /**
         * Post's group
         */
        public BigInteger n;

        /**
         * Post's exponent
         */
        public BigInteger e;
        
        /**
         * Create a new keypost.
         * @param n
         * @param e
         */
        public keyPost(BigInteger n, BigInteger e){
            this.n = n;
            this.e = e;
        }
    }
    
    private SecurePseudoGenerator pRNG = new SecurePseudoGenerator(BlumBlumShub);
    private PrimeGenerator pgen = new PrimeGenerator();
    
    private BigInteger p;
    private BigInteger q;
    private BigInteger phiN;
    private BigInteger e;
    private BigInteger d;
    private BigInteger n;
    
    /**
     * Initialize RSA with no information. p, q, and e will be generated.
     * @param sizes prime digit size.
     * @throws Exception
     */
    public RSA(int sizes) throws Exception{
        while (true){
            p = pgen.generatePrime(pRNG, sizes);
            q = pgen.generatePrime(pRNG, sizes);
            if (!p.equals(q)){
                break;
            }
        }
        n = p.multiply(q);
        phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = pgen.generateBZx(phiN);
        d = Algorithms.cyclicInverse(e, phiN).mod(phiN);
        System.out.println("Initialized RSA, p: " + p + " q : " + q + " e : " + e + "d " + d);
    }
    
    /**
     * Initialize RSA with pre-made group and exponent.
     * @param n group
     * @param e exponent
     */
    public RSA(BigInteger n,BigInteger e){
        this.n = n;
        this.e = e;
        System.out.println("Initialized RSA, n: " + n + " e: " + e);
    }
    
    /**
     * Initialize RSA with pre-made p,q, and e.
     * @param p prime p
     * @param q prime q
     * @param e exponent
     * @throws Exception
     */
    public RSA(BigInteger p, BigInteger q, BigInteger e) throws Exception{
        this.p = p;
        this.q = q;
        phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = e;
        n = p.multiply(q);
        d = Algorithms.cyclicInverse(e, phiN).mod(phiN);
        System.out.println("Initialized RSA, p: " + p + " q : " + q + " e : " + e + " d : " + d);
    }
    
    /**
     * Publish public data.
     * @return
     */
    public keyPost post(){
        System.out.println("Posting...");
        System.out.println("n : " + n);
        System.out.println("e : " + e);
        return new keyPost(n,e);
    }
   
    
    //Decrypt a message that could be received by this RSA client. "Bob".

    /**
     * Decrypt data encrypted with public key.
     * @param text Ciphertext to be decrypted. Do not use this to encrypt.
     * @return decrypted text.
     */
    public BigInteger decrypt(BigInteger text){
       
        return Algorithms.fastExponentiate(text, d, n);
    }
    
    /**
     * Encrypt text with public key data.
     * @param text value to encrypt
     * @return encrypted cipher.
     */
    public BigInteger encrypt(BigInteger text){
        //System.out.println("Encrypting " + text + ".");
        return Algorithms.fastExponentiate(text, e, n);
    }
    
    /**
     * Initialize a new RSA object that can act as an RSA initializer. 
     * This allows an eavesdropper to encrypt or decrypt on a channel.
     * 
     * @param n group
     * @param e public exponent
     * @return new RSA object.
     * @throws Exception
     */
    public static RSA maliciousCrypt(BigInteger n, BigInteger e) throws Exception{
        if (!new PrimeGenerator().probablyPrime(n) && !n.equals(BigInteger.ZERO)){
            BigInteger g = Algorithms.pollardsRho(n);
            BigInteger p = n.divide(g);
            BigInteger q = g;
            RSA crack = new RSA(p,q,e);
            return crack;
        }else{
            return null;
        }
    }
    
}
