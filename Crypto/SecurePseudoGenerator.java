/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import static Crypto.SecurePseudoGenerator.Generator.*;
import L1.Algorithms;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Liam Pierce
 */

public class SecurePseudoGenerator implements generator {
    private BigInteger p;
    private BigInteger q;
    private int pquse = 0; //Fix convergence issues with certain pq pairs.
    private BigInteger seed;
    
    private PrimeGenerator insecurePrimes = new PrimeGenerator();
    private Generator generationMethod;
    public enum Generator{
        BlumBlumShub,
        NaorReingold;
    }
    
    public SecurePseudoGenerator(Generator generationMethod){
        this.generationMethod = generationMethod;
    }
    
    public SecurePseudoGenerator(){
        this.generationMethod = NaorReingold;
    }
    
    public BigInteger insecurePrime(int bit) throws Exception{
        BigInteger prime = insecurePrimes.generatePrime(insecurePrimes, bit);
        return prime;
    }
    
    private BigInteger BBS(int digits){
        try {
            
            if (p == null || q == null || pquse > 1100){
                pquse = 0;
                while (true){ //Generate a suitable p and q.
                    p = insecurePrimes.generateBZx34(digits);
                    q = insecurePrimes.generateBZx34(digits);
                    if (!q.equals(p)){
                        break;
                    }
                }
            }else{
                pquse += 1;
            }
           
            BigInteger n = p.multiply(q); // Can be reused, I don't for simplicity.
            
            BigInteger seed = insecurePrimes.generateBZx(n);
            BigInteger result = BigInteger.ONE; // This ensures a smaller range of sizes.
            
            for (int i = 0;i < (int) Math.ceil(Math.log10(Math.pow(10,digits))/Math.log10(2));i++){
                result = result.shiftLeft(1).or(seed.mod(BigInteger.valueOf(2)));
                
                seed = seed.pow(2).mod(n);
                //System.out.println(Integer.toBinaryString(result.intValue()));
            }
            //System.out.println("generated : " + result);
            return result;
            
        } catch (Exception ex) {
            System.out.println("Exception " + ex );
            ex.printStackTrace();
            Logger.getLogger(SecurePseudoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return BigInteger.ZERO;
    }
    
    private BigInteger NR(int n){
        BigInteger p;
        BigInteger q;
        try {
            p = insecurePrime(n);
            q = insecurePrime(n);
            
            BigInteger[] randoms = new BigInteger[2 * n];
            
            BigInteger N = p.multiply(q);
            
            for (int i = 0;i< 2*n;i++){
                randoms[i] = insecurePrimes.generateB(N);
            }
            
            BigInteger sqr = insecurePrimes.generateBZx(N);
            BigInteger g  = Algorithms.fastExponentiate(sqr, BigInteger.valueOf(2), N);
            
        } catch (Exception ex) {
            Logger.getLogger(SecurePseudoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return BigInteger.ZERO;
    }
    
    @Override
    public BigInteger generateNumber(int digits){
        switch (this.generationMethod) {
            case BlumBlumShub:
                return BBS(digits);
            case NaorReingold:
                return NR(digits);
            default:
                return BigInteger.ZERO;
        }
        
    }
}
