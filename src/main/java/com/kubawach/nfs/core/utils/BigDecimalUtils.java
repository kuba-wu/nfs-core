package com.kubawach.nfs.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtils {

	public static BigDecimal div(long one, long two) {
		return BigDecimal.valueOf(one).divide(BigDecimal.valueOf(two), RoundingMode.HALF_EVEN);
	}
	
	public static BigDecimal div(BigDecimal one, long two) {
		return one.divide(BigDecimal.valueOf(two), RoundingMode.HALF_EVEN);
	}
}
