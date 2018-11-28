/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1;

import Crypto.PrimeGenerator;
import Crypto.generator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Liam Pierce
 */
public class Algorithms {
    
    
    
    public static HashMap<BigInteger,Boolean> primeFactorization(BigInteger n){
        //System.out.println("Starting prime factorization of " + n);
        System.err.flush();
        BigInteger inter = n;
        HashMap<BigInteger,Boolean> factors = new HashMap<>();
        if (inter.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            factors.put(BigInteger.valueOf(2),true);
            //System.out.println("Factor : 2");
        }
        
        while (inter.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            inter = inter.divide(BigInteger.valueOf(2));
        }
        
        for (int i = 3;i<=Math.sqrt(i);i+=2){
            System.out.println(i);
            System.err.flush();
            while (inter.mod(BigInteger.valueOf(i)).equals(BigInteger.ZERO)){
                //System.out.println("Factor : " + i);
                factors.put(BigInteger.valueOf(i),true);
                inter = inter.divide(BigInteger.valueOf(i));
            }
        }
        if (factors.isEmpty()){
            factors.put(n, true);
        }
        return factors;
    }
   
    
    /* https://stackoverflow.com/questions/4407839/how-can-i-find-the-square-root-of-a-java-biginteger */
    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }
    
    private static BigInteger euclideanRecurse(BigInteger a, BigInteger b){
        BigInteger remainder = a.mod(b);
        //System.out.printf("%d / %d = %d, remainder %d; %d * %d + %d\r\n",a,b,(a.subtract(a.mod(b))).divide(b),a.mod(b),(a.subtract(a.mod(b))).divide(b),b,a.mod(b));
        if (remainder.compareTo(BigInteger.ZERO) == 0){
            return b;
        }
        return euclideanRecurse(b,remainder);
    }
    
    public static BigInteger euclidean(BigInteger a, BigInteger b) throws Exception{
        if (a.equals(0) || b.equals(0)){
            throw new Exception();
        }
        //System.out.println(a + " " + b);
        if (a.compareTo(b) < 0){
            //System.out.printf("Euclidean (%d, %d )\r\n",b,a);
            BigInteger gcd = euclideanRecurse(b,a);
            //System.out.printf("GCD(%d, %d) = %d\r\n",a,b,gcd);
        }
        //System.out.println("====");
        //System.out.printf("Euclidean (%d, %d)\r\n",a,b);
        BigInteger gcd = euclideanRecurse(a,b);
        //System.out.printf("GCD(%d, %d) = %d\r\n",a,b,gcd);
        return gcd;
    }
    
    //https://introcs.cs.princeton.edu/java/99crypto/ExtendedEuclid.java.html
    // ^ Wasnt here for class.
    public static BigInteger[] extendedEuclideanRecursor(BigInteger p, BigInteger q){
        if (q.equals(BigInteger.ZERO)){
            return new BigInteger[] { p, BigInteger.ONE, BigInteger.ZERO };
        }
        
        BigInteger[] previous = extendedEuclideanRecursor(q,p.mod(q)); // Lecture 1, #32.
        BigInteger gcd = previous[0];
        BigInteger a   = previous[2];
        BigInteger b   = previous[1].subtract(p.divide(q).multiply(previous[2]));
        
        //System.out.printf(" d: %-10d, a : %-10d, b: %-10d \r\n", gcd,a,b);
        
        return new BigInteger[]{gcd,a,b};
    }
    
    public static BigInteger[] extendedEuclidean(BigInteger p,BigInteger q){
        BigInteger[] recursor = extendedEuclideanRecursor(p,q);
        //System.out.println(recursor[1] + "(" + p + ") + " + recursor[2] + "(" + q + ") = " + recursor[0]);
        return recursor;
    }
    
    public static BigInteger cyclicInverse(BigInteger n, BigInteger mod){
        BigInteger[] f = extendedEuclidean(n,mod);
        if (!f[0].equals(BigInteger.ONE)){
            //System.out.println("Not coprime. Not in set Zxp.");
            return BigInteger.ZERO;
        }
        //System.out.printf("%d^-1 mod %d = %d\r\n",n,mod,f[1]);
        return f[1];
    }
    
    //x^e % mod
    private static BigInteger fastExponentiateRecurse(BigInteger x,BigInteger e, BigInteger y,BigInteger mod){
        //System.out.println(" x: " + x + " e:" + e + " y:" + y);
                 
        //System.out.printf("%-25d%-25d%-25d\r\n",x,e,y);
        if (e.equals(BigInteger.ZERO)){
            //System.out.println("-----\r\ne = 0; finished.");
            return y;
        }
        
        if (e.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            
            return fastExponentiateRecurse(x.pow(2).remainder(mod),e.shiftRight(1),y,mod);
        }else{
            return fastExponentiateRecurse(x,e.subtract(BigInteger.ONE),y.multiply(x).mod(mod),mod);
        }
    }
    
    public static BigInteger fastExponentiate(BigInteger x, BigInteger pow, BigInteger mod){
        //System.out.printf("X                    E                    Y                  action\r\n");
        
        return Algorithms.fastExponentiateRecurse(x, pow, BigInteger.ONE, mod);
        //System.out.printf("%d ^ %d mod %d = %d \r\n",x,pow,mod,Algorithms.fastExponentiateRecurse(x, pow, BigInteger.ONE, mod));
        //return BigInteger.ZERO;
    }
    /*
    public static BigInteger cyclicInverse(BigInteger n,BigInteger phin,BigInteger mod){
        return fastExponentiate(n,phin,mod);
    }
    */
   
    
    public static BigInteger primativeRootSearch(PrimeGenerator pRNG, BigInteger group){
        //System.out.println("Starting root search");
        BigInteger phi = group.subtract(BigInteger.ONE);
        BigInteger b;
        HashMap<BigInteger,Boolean> factors = primeFactorization(phi);
        while (true){
            b = pRNG.generateB(group);
            if (b.compareTo(BigInteger.valueOf(2)) < 0){
                continue;
            }
            //System.out.println("Testing possible primative root " + b);
            boolean generator = true;
            for (BigInteger c : factors.keySet()){
                if (fastExponentiate(b,(phi.divide(c)),group).equals(BigInteger.ONE)){
                    generator = false;
                    break;
                }
            }
            if (generator){
                //System.out.println("Using primative root " + b);
                return b;
            }
      
        }
    }
    
    public static int phi(BigInteger n){
        int phireturn = 0;
        for (BigInteger i = BigInteger.valueOf(1);i.compareTo(n)<0;i=i.add(BigInteger.ONE)){
            if (n.gcd(i).equals(BigInteger.valueOf(1))){
                System.out.println(i);
                phireturn += 1;
            }
        }
        System.out.println(phireturn);
        return phireturn;
    }
    
    public static BigInteger babyStepGiantStep(BigInteger p,BigInteger generator,BigInteger a){
        BigInteger n = p.subtract(BigInteger.ONE);
        BigInteger m = sqrt(n);
        BigInteger c = fastExponentiate(cyclicInverse(generator,p).mod(p),m,p).mod(p);
        //System.out.println(c);
        HashMap<BigInteger,BigInteger> babyStep  = new HashMap<>();
        
        for (BigInteger j = BigInteger.ZERO; m.compareTo(j) > 0;j=j.add(BigInteger.ONE)){
            babyStep.put(fastExponentiate(generator,j,p), j);
        }
        
        for (BigInteger i = BigInteger.ZERO;i.compareTo(m.subtract(BigInteger.ONE)) < 0;i = i.add(BigInteger.ONE)){
            BigInteger val = a.multiply(fastExponentiate(c,i,p)).mod(p);
            //System.out.println(val);
            if (babyStep.containsKey(val)){
                BigInteger setj = babyStep.get(val);
                return i.multiply(m).add(setj);
            }
        }
        return BigInteger.ZERO;
    }
    
    public static BigInteger pollardsRho(){
        return BigInteger.ZERO;
    }

    
    
    
}
