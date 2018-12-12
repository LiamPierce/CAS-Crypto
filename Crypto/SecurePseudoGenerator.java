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

    /**
     * Generator algorithm choice.
     */
    public enum Generator{

        BlumBlumShub,
        NaorReingold;
    }
    
    /**
     * Create a new generator object with the given generation algorithm.
     * @param generationMethod algorithm to be used.
     */
    public SecurePseudoGenerator(Generator generationMethod){
        this.generationMethod = generationMethod;
    }
    
    /**
     * Create a new generator with the NaorReingold algorithm as default.
     */
    public SecurePseudoGenerator(){
        this.generationMethod = NaorReingold;
    }
    
    /**
     *
     * Generate a prime using an insecure random generator for use in
     * secure prime generation.
     * 
     * @param digits number of digits
     * @return new insecure prime
     * @throws Exception
     */
    public BigInteger insecurePrime(int digits) throws Exception{
        BigInteger prime = insecurePrimes.generatePrime(insecurePrimes, digits);
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
            
            BigInteger build = BigInteger.ONE;
            
            BigInteger[] randoms = new BigInteger[2 * n];
            
            BigInteger N = p.multiply(q);
            
            for (int i = 0;i< 2*n;i++){
                randoms[i] = insecurePrimes.generateB(N);
            }
            
            BigInteger sqr = insecurePrimes.generateBZx(N);
            BigInteger g  = Algorithms.fastExponentiate(sqr, BigInteger.valueOf(2), N);
            BigInteger sx = BigInteger.ONE;
            
            
            while (build.toString().length() < n){
                BigInteger asum = BigInteger.ZERO;

                for (int i = 0;i<n / 2;i++){
                    BigInteger bitValue = sx.and(BigInteger.ONE); //Mask first bit.
                    asum = asum.add(randoms[(int) Math.ceil(i * 2) + bitValue.intValue()]);
                    sx = sx.shiftRight(1);
                }

                BigInteger ba = Algorithms.fastExponentiate(g,asum,N);
                BigInteger r = insecurePrimes.generateNumber(n * 2);
                
                build = build.shiftLeft(1).add(ba.multiply(r).mod(BigInteger.valueOf(2)));
                
                if (build.toString().length() == n){
                    break;
                }
                
                sx = sx.add(BigInteger.ONE);
            }
            
            return build;
            
        } catch (Exception ex) {
            Logger.getLogger(SecurePseudoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return BigInteger.ZERO;
    }
    
    /**
     * Generate a random, cryptographically secure number.
     * @param digits number of digits in output.
     * @return new random number.
     */
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
