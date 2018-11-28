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
    public class keyPost{
        public BigInteger n;
        public BigInteger e;
        
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
    
    public RSA(int sizes) throws Exception{
        while (true){
            p = pgen.generatePrime(pRNG, sizes);
            q = pgen.generatePrime(pRNG, sizes);
            if (!p.equals(q)){
                break;
            }
        }
        phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = pgen.generateBZx(phiN);
        d = Algorithms.cyclicInverse(e, phiN).mod(phiN);
        System.out.println("Initialized RSA, p: " + p + " q : " + q + " e : " + e + "d " + d);
    }
    
    public RSA(BigInteger p, BigInteger q, BigInteger e) throws Exception{
        this.p = p;
        this.q = q;
        phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = e;
        d = Algorithms.cyclicInverse(e, phiN).mod(phiN);
        System.out.println("Initialized RSA, p: " + p + " q : " + q + " e : " + e + "d " + d);
    }
    
    public keyPost post(){
        System.out.println("Posting...");
        System.out.println("n : " + p.multiply(q));
        System.out.println("e : " + e);
        return new keyPost(p.multiply(q),e);
    }
   
    
    //Decrypt a message that could be received by this RSA client. "Bob".
    public BigInteger localAction(BigInteger text){
       
        return Algorithms.fastExponentiate(text, d, p.multiply(q));
    }
    
    public BigInteger publicAction(BigInteger text){
        return Algorithms.fastExponentiate(text, e, p.multiply(q));
    }
    
    public static RSA maliciousCrypt(BigInteger n, BigInteger e) throws Exception{
        if (!new PrimeGenerator().probablyPrime(n) && !n.equals(BigInteger.ZERO)){
            BigInteger x = BigInteger.valueOf(2);
            BigInteger y = x.pow(2).add(BigInteger.ONE);
            BigInteger g;
            while (true){
                g = Algorithms.euclidean(x.subtract(y).mod(n), n);
                if (g.compareTo(BigInteger.ONE) > 0 && g.compareTo(n) <= 0){
                    System.out.println(g);
                    break;
                }else if (g.equals(BigInteger.ONE)){
                    x = x.pow(2).add(BigInteger.ONE).mod(n);
                    y = y.pow(2).add(BigInteger.ONE).pow(2).add(BigInteger.ONE).mod(n);
                }else if (g.equals(n)){
                    x = BigInteger.valueOf(3);
                    y = x.pow(2).add(BigInteger.ONE);
                }
            }
            BigInteger p = n.divide(g);
            BigInteger q = g;
            RSA crack = new RSA(p,q,e);
            return crack;
        }else{
            return null;
        }
    }
    
}
