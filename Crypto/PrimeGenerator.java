/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import L1.Algorithms;
import static java.lang.System.exit;
import java.math.BigInteger;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * @author Liam Pierce
 * 
 * Generates primes for use in cryptographically secure pRNDs.
 * 
 * Uses fermat test, then miller rabin test.
 */
public class PrimeGenerator implements generator {
   
    private class mResult{
        public BigInteger twor;
        public BigInteger r;
        
        public mResult(BigInteger twor, BigInteger r){
            this.twor = twor;
            this.r    = r;
            
        }
    }
    
    public static int kcertainty = 100; //1-(1/4)^k
    
    /*
    * Generates large numbers that probably aren't very random.
    */
    @Override
    public BigInteger generateNumber(int digits){
        IntStream generator = new Random().ints();
        BigInteger generated = BigInteger.valueOf(Math.abs(generator.iterator().next()));
        int digitCount = generated.toString().length();
        if (digitCount > digits){
            return generated.mod(BigInteger.TEN.pow(digits));
        }else if (digitCount < digits){
            return generated.add(generateNumber(digits - digitCount).multiply(BigInteger.TEN.pow(digitCount)));
        }else{
            return generated;
        }
    }
    
    public int generateInt(int min, int max){
        return Math.abs(new Random().nextInt(max)) + min;
    }
    
    public BigInteger generateB(BigInteger max){
        if (max.equals(BigInteger.ZERO)){
            exit(1);
        }
        return generateNumber(generateInt(1,max.toString().length())).mod(max.abs());
    }
    
    public BigInteger generateBZx(BigInteger group) throws Exception{
        BigInteger generated;
        while (true){
            generated = generateB(group.subtract(BigInteger.ONE));
            if (generated.compareTo(BigInteger.ZERO) > 0){
                if (Algorithms.euclidean(generated, group).equals(BigInteger.ONE)){
                    return generated;
                }
            }
        }
    }
    
    public BigInteger generateBZx34(int digits) throws Exception{
        BigInteger generated;
        while (true){
            generated = generateBZx(generatePrime(this,digits));
            if (generated.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))){
                return generated;
            }
        }
    }
    
    private mResult mCalc(BigInteger init){
        BigInteger rider  = init;
        BigInteger factor = BigInteger.ONE;
        BigInteger r      = BigInteger.ZERO;
        while (true){
            if (rider.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
                rider  = rider.divide(BigInteger.valueOf(2));
                factor = factor.multiply(BigInteger.valueOf(2));
                r      = r.add(BigInteger.ONE);
            }else{
                break;
            }
        }
        return new mResult(factor,r);
    }
    
    public boolean probablyPrime(BigInteger n) throws Exception{
        boolean probable = true;
            
        if (n.equals(BigInteger.ONE) || n.equals(BigInteger.ZERO) || n.mod(BigInteger.valueOf(2)).equals(1)){
            return false;
        }

        if (probable){
            for (int i = 0;i<10;i++){
                BigInteger b = generateB(n);
                if (b.equals(BigInteger.ZERO)){
                    return false;
                }
                if (Algorithms.euclidean(b, n).equals(BigInteger.ONE)){
                    if (Algorithms.fastExponentiate(b, n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE)){
                        //maybe prime?
                    }else{
                        probable = false;
                        break;
                    }
                }
            }
        }
        if (probable){
            int bcount = 0;
            mResult res     = mCalc(n.subtract(BigInteger.ONE));
            if (res.r.equals(BigInteger.ZERO)){
                return false;
            }
            BigInteger r    = res.r;
            BigInteger twoR = res.twor;
            BigInteger m    = n.subtract(BigInteger.ONE).divide(twoR);
            //System.out.println("r: " + r +" M : " + m + ", twoR : " + twoR);
            
            while (bcount < kcertainty){
                
                BigInteger b = generateB(n);
                
                if (b.equals(BigInteger.ZERO)){
                    continue;
                }
                if (Algorithms.euclidean(b, n).equals(BigInteger.ONE)){
                    bcount += 1;
                    if ((Algorithms.fastExponentiate(b, m, n).equals(BigInteger.ONE))){
                        break;
                    }else{
                       for (int i = 0;i<r.intValue();i++){
                           
                           if (Algorithms.fastExponentiate(b, m.multiply(BigInteger.valueOf((int) Math.pow(2,i))), m).equals(n.subtract(BigInteger.ONE))){
                               break;
                           }
                       }
                       probable = false;
                       break;
                    }
                }
            }
            if (probable){
                return true;
            }

        }
        return false;
    }
    
    public BigInteger generatePrime(generator numberGenerator,int digits) throws Exception{
        
        BigInteger n;
        while (true){
            n = numberGenerator.generateNumber(digits);
            
            if (n.compareTo(BigInteger.valueOf(2)) < 0){
                continue;
            }
            
            //System.out.println("Testing " + n + ".");
            
            if (probablyPrime(n)){
                break;
            }
        }
        //System.out.println("PROBABLE : " + n);
        return n;
    }
    
}
