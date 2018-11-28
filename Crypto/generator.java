/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import java.math.BigInteger;

/**
 *
 * @author Liam Pierce
 */
public interface generator{
    BigInteger generateNumber(int digits);
}
