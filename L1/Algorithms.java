/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1;

import Crypto.PrimeGenerator;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Liam Pierce
 */
public class Algorithms {
    
    /**
     * Factor a number to a certain cap.
     * 
     * @param n number to factor
     * @param cap factorization cap
     * @return HashMap of Prime -> Occurrence.
     */
    public static HashMap<BigInteger,Integer> cappedPrimeFactorization(BigInteger n,BigInteger cap){
        //System.out.println("Starting prime factorization of " + n);
        //System.err.flush();
        BigInteger inter = n;
        HashMap<BigInteger,Integer> factors = new HashMap<>();
        
        while (inter.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            if (factors.get(BigInteger.valueOf(2)) != null){
                factors.put(BigInteger.valueOf(2),factors.get(BigInteger.valueOf(2)) + 1);
            }else{
                factors.put(BigInteger.valueOf(2),1);
            }
            inter = inter.divide(BigInteger.valueOf(2));
        }
        
        for (BigInteger i=BigInteger.valueOf(3);i.compareTo(cap) <= 0;i=i.add(BigInteger.valueOf(2))){
            
            while (inter.mod(i).equals(BigInteger.ZERO)){
                //System.out.println("Factor : " + i);
                if (factors.get(i) != null){
                    factors.put(i,factors.get(i) + 1);
                }else{
                    factors.put(i,1);
                }
                inter = inter.divide(i);
            }
        }
        if (factors.isEmpty()){
            factors.put(n, 1);
        }
        
        return factors;
    }
    
    /**
     * Factor a number n and return the prime factorization.
     * 
     * @param n number to factor.
     * @return HashMap Prime -> Occurrence.
     */
    public static HashMap<BigInteger,Integer> primeFactorization(BigInteger n){
        return cappedPrimeFactorization(n,sqrt(n));
    }
   
    
    /* https://stackoverflow.com/questions/4407839/how-can-i-find-the-square-root-of-a-java-biginteger */
    /**
     * Calculate the square root of a BigInteger x.
     * 
     * @param x number to find the square root of
     * @return same as Math.sqrt(x) but with BigIntegers.
     */

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
    
    /**
     * Find the output of Euclidean algorithm.
     * 
     * @param a
     * @param b
     * @return The gcd of a and b.
     * @throws Exception
     */
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
    
    private static BigInteger[] extendedEuclideanRecursor(BigInteger p, BigInteger q){
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
    
    /**
     * Calculate extended euclidean algorithm.
     * @param p
     * @param q
     * @return 
     */
    public static BigInteger[] extendedEuclidean(BigInteger p,BigInteger q){
        BigInteger[] recursor = extendedEuclideanRecursor(p,q);
        //System.out.println(recursor[1] + "(" + p + ") + " + recursor[2] + "(" + q + ") = " + recursor[0]);
        return recursor;
    }
    
    /**
     * Find the cyclic inverse of n in group mod.
     * @param n number to find inverse of
     * @param mod the modulus to find the inverse in.
     * @return the inverse.
     */
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
    
    /**
     * Fast exponentiation function.
     * 
     * @param x number
     * @param pow power
     * @param mod modulus
     * @return x^pow % mod
     */
    public static BigInteger fastExponentiate(BigInteger x, BigInteger pow, BigInteger mod){
        //System.out.printf("X                    E                    Y                  action\r\n");
        
        return Algorithms.fastExponentiateRecurse(x, pow, BigInteger.ONE, mod);
        //System.out.printf("%d ^ %d mod %d = %d \r\n",x,pow,mod,Algorithms.fastExponentiateRecurse(x, pow, BigInteger.ONE, mod));
        //return BigInteger.ZERO;
    }
   
    /**
     * Find a primitive root of group.
     * @param pRNG Random number generator
     * @param group the group to find a primitive root of.
     * @return a primitive root of group.
     */
    public static BigInteger primitiveRootSearch(PrimeGenerator pRNG, BigInteger group){
        //System.out.println("Starting root search");
        BigInteger phi = group.subtract(BigInteger.ONE);
        BigInteger b;
        HashMap<BigInteger,Integer> factors = primeFactorization(phi);
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
    
    /**
     * Find the size of a group n. Not tractable for larger n's.
     * 
     * @param n the group.
     * @return the size of the group.
     */
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
    
    /**
     * Solve the discrete log problem.
     * @param p the group.
     * @param generator the generator
     * @param a b^secret of unknown secret..
     * @return secret
     */
    public static BigInteger babyStepGiantStep(BigInteger p,BigInteger generator,BigInteger a){
        BigInteger n = p.subtract(BigInteger.ONE);
        BigInteger m = sqrt(n);
        BigInteger c = fastExponentiate(cyclicInverse(generator,p).mod(p),m,p).mod(p);
        //System.out.println(c);
        HashMap<BigInteger,BigInteger> babyStep  = new HashMap<>();
        //System.out.println("J-pass start.");
        for (BigInteger j = BigInteger.ZERO; m.compareTo(j) > 0;j=j.add(BigInteger.ONE)){
            if (j.mod(m.divide(m.divide(BigInteger.valueOf(200)))).equals(BigInteger.ZERO)){
                //System.out.println("J-value :" + j + " / " + m);
            }
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
    
    /**
     *
     */
    public static class indexState{

        /**
         *
         */
        public HashMap<BigInteger,Integer> factorBase = new HashMap<>();

        /**
         *
         */
        public HashMap<BigInteger,BigInteger> symbolTable = new HashMap<>();
        
        /**
         *
         */
        public BigInteger group;

        /**
         *
         */
        public BigInteger generator;
        
        /**
         *
         * @param group
         * @param generator
         */
        public indexState(BigInteger group, BigInteger generator){
            this.group = group;
            this.generator = generator;
        }
        
        /**
         *
         * @param p
         * @return
         */
        public boolean enlistPrimeFactor(BigInteger p){
            if (factorBase.containsKey(p)){
                factorBase.put(p, factorBase.get(p) + 1);
                return true;
            }else{
                factorBase.put(p,1);
                return false;
            }
        }
    }
    
    /**
     * Factor n.
     * 
     * @param n number to be factored.
     * @return a factor of n.
     * @throws Exception
     */
    public static BigInteger pollardsRho(BigInteger n) throws Exception{
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
        return g;
    }
    
    /**
     * Factor n.
     * @param n number to factor.
     * @return a factor of n.
     * @throws Exception
     */
    public static BigInteger pollardsPMinusOne(BigInteger n) throws Exception{
        PrimeGenerator k = new PrimeGenerator();
        BigInteger b;
        BigInteger x;
        BigInteger g;
        
        boolean factorn = false;
        
        while (true){
            factorn = false;
            b = k.generateB(n.subtract(new BigInteger("2")));
            x = b;
            if (x.compareTo(BigInteger.ONE) <= 0){
                continue;
            }
            
            
            g = Algorithms.euclidean(x, n);
            System.out.println("GCD : " + g);
                    
            if (g.compareTo(n) < 0 && g.compareTo(BigInteger.ONE) > 0){
                System.out.println("Factor @A.");
                break;
            }else if (g.equals(BigInteger.ONE)){
                HashMap<BigInteger, Integer> factorization = primeFactorization(x);
                BigInteger[] factors = factorization.keySet().toArray(new BigInteger[factorization.size()]);
                Arrays.sort(factors);
                
                for (BigInteger factor : factors) {
                    int l = (int) (Math.log10(n.doubleValue()) / Math.log(factor.doubleValue()));
                    x = fastExponentiate(x, factor.pow(l), n);
                    g = Algorithms.euclidean(fastExponentiate(x, factor.pow(l), n.subtract(BigInteger.ONE)), n);
                    if (g.compareTo(n) < 0 && g.compareTo(BigInteger.ONE) > 0){
                        System.out.println("Factor : " + factor + " confirmed factor.");
                        factorn = true;
                        break;
                    }else if (g.equals(n)){
                        break;
                    }
                }
                //factorn false => no prime factors?
                
            }
            
            
            if (factorn){
                break;
            }
            
        }
        
        return g;
    }
    
    private static HashMap<String,indexState> indexStates = new HashMap<>();
    
    /**
     *
     * @param group
     * @param generator
     * @param bl
     * @return
     */
    public static BigInteger indexCalculus(BigInteger group,BigInteger generator,BigInteger bl){
        indexState state = indexStates.get(group + ":" + generator);
        if (state == null){
            state = new indexState(group, generator);
        }
        
        PrimeGenerator pRNG = new PrimeGenerator();
        
        int loops = 0;
        int success = 0;
        while (true){
            loops += 1;
            BigInteger k = pRNG.generateB(group.subtract(BigInteger.valueOf(1)));
            BigInteger b = fastExponentiate(generator,k,group);
            System.out.printf("k: %15d, b: %15d, ",k,b);
            for (BigInteger p : cappedPrimeFactorization(b,BigInteger.valueOf(2000)).keySet()){
                System.out.println(p);
                if (!state.enlistPrimeFactor(p)){ // new prime
                    success += 1;
                }
            }
            
            if (loops > 20 && ((double) success / (double) loops) < 1 && state.factorBase.size() > 6){
                break;
            }
        }
        
        int a_max = 0;
        int b_max = 0;
        int c_max = 0;
        for (int i : state.factorBase.values()){
            if (i < c_max){
                continue;
            }
            if (i > a_max){
                c_max = b_max;
                b_max = a_max;
                a_max = i;
            }else if (i > b_max){
                c_max = b_max;
                b_max = i;
            }else{
                c_max = i;
            }
        }
        final int min = c_max;
        
        state.factorBase = state.factorBase.entrySet().stream().filter((pair)->{
            return pair.getValue() >= min;
        }).collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue,
	(oldValue, newValue) -> oldValue, HashMap::new));
        
        while (true){
            BigInteger k = pRNG.generateB(group.subtract(BigInteger.valueOf(1)));
            BigInteger b = fastExponentiate(generator,k,group);
            //System.out.printf("k: %15d, b: %15d, ",k,b);
            
            HashMap<BigInteger,Integer> factorization = primeFactorization(b);
            boolean failure = false;
            /*
            Optional<BigInteger> reaccumulation = factorization.entrySet()
                                            .stream()
                                            .map((e)->e.getKey().multiply(BigInteger.valueOf(e.getValue())))
                                            .reduce((e,f)->e.multiply(f));
            
            
            if (!reaccumulation.isPresent() || !reaccumulation.get().equals(b)){
                System.out.println("Cap failure.");
                continue;
            }else{
                System.out.println("Cap success.");
            }
            
            */
            for (BigInteger p : factorization.keySet()){
                if (!state.factorBase.containsKey(p)){
                    //System.out.println("Factor base has no prime " + p);
                    failure = true;
                    break;
                }
            }
            
            if (failure){
                
                continue;
            }
            // k = count * log generator (prime1) + count * log generator (prime2).
            if (factorization.size() == 1){
                HashMap.Entry<BigInteger,Integer> item = factorization.entrySet().iterator().next();
                state.symbolTable.put(item.getKey(), k.divide(BigInteger.valueOf(item.getValue())));
                System.out.println("logb" + generator + "(" + item.getKey() + ") = " + k.divide(BigInteger.valueOf(item.getValue())));
            }else{
                System.out.println(state.symbolTable);
                int notFillable = 0;
                for (HashMap.Entry item : factorization.entrySet()){
                    if (!state.symbolTable.containsKey(item.getKey())){
                        notFillable += 1;
                    }
                }
                
                if (notFillable == 1 && factorization.size() > 1){
                    System.out.println("Populatable");
                }else{
                    System.out.println("Not populatable");
                }
                    
            }
            
            System.out.println("Works! : " + b + " Primes : " + factorization);
        }
       
    }
   
    
    
}
