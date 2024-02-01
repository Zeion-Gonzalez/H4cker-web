package org.jcodec.common.model;

import org.jcodec.common.StringUtils;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class RationalLarge {
    final long den;
    final long num;
    public static final Rational ONE = new Rational(1, 1);
    public static final Rational HALF = new Rational(1, 2);
    public static final RationalLarge ZERO = new RationalLarge(0, 1);

    public RationalLarge(long num, long den) {
        this.num = num;
        this.den = den;
    }

    public long getNum() {
        return this.num;
    }

    public long getDen() {
        return this.den;
    }

    public static Rational parse(String string) {
        String[] split = StringUtils.split(string, ":");
        return new Rational(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public int hashCode() {
        int result = ((int) (this.den ^ (this.den >>> 32))) + 31;
        return (result * 31) + ((int) (this.num ^ (this.num >>> 32)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            RationalLarge other = (RationalLarge) obj;
            return this.den == other.den && this.num == other.num;
        }
        return false;
    }

    public long multiplyS(long scalar) {
        return (this.num * scalar) / this.den;
    }

    public long divideS(long scalar) {
        return (this.den * scalar) / this.num;
    }

    public long divideByS(long scalar) {
        return this.num / (this.den * scalar);
    }

    public RationalLarge flip() {
        return new RationalLarge(this.den, this.num);
    }

    /* renamed from: R */
    public static RationalLarge m2134R(long num, long den) {
        return new RationalLarge(num, den);
    }

    /* renamed from: R */
    public static RationalLarge m2133R(long num) {
        return m2134R(num, 1L);
    }

    public boolean lessThen(RationalLarge sec) {
        return this.num * sec.den < sec.num * this.den;
    }

    public boolean greaterThen(RationalLarge sec) {
        return this.num * sec.den > sec.num * this.den;
    }

    public boolean smallerOrEqualTo(RationalLarge sec) {
        return this.num * sec.den <= sec.num * this.den;
    }

    public boolean greaterOrEqualTo(RationalLarge sec) {
        return this.num * sec.den >= sec.num * this.den;
    }

    public boolean equals(RationalLarge other) {
        return this.num * other.den == other.num * this.den;
    }

    public RationalLarge plus(RationalLarge other) {
        return MathUtil.reduce((this.num * other.den) + (other.num * this.den), this.den * other.den);
    }

    public RationalLarge plus(Rational other) {
        return MathUtil.reduce((this.num * other.den) + (other.num * this.den), this.den * other.den);
    }

    public RationalLarge minus(RationalLarge other) {
        return MathUtil.reduce((this.num * other.den) - (other.num * this.den), this.den * other.den);
    }

    public RationalLarge minus(Rational other) {
        return MathUtil.reduce((this.num * other.den) - (other.num * this.den), this.den * other.den);
    }

    public RationalLarge plus(long scalar) {
        return new RationalLarge(this.num + (this.den * scalar), this.den);
    }

    public RationalLarge minus(long scalar) {
        return new RationalLarge(this.num - (this.den * scalar), this.den);
    }

    public RationalLarge multiply(long scalar) {
        return new RationalLarge(this.num * scalar, this.den);
    }

    public RationalLarge divide(long scalar) {
        return new RationalLarge(this.den * scalar, this.num);
    }

    public RationalLarge divideBy(long scalar) {
        return new RationalLarge(this.num, this.den * scalar);
    }

    public RationalLarge multiply(RationalLarge other) {
        return MathUtil.reduce(this.num * other.num, this.den * other.den);
    }

    public RationalLarge multiply(Rational other) {
        return MathUtil.reduce(this.num * other.num, this.den * other.den);
    }

    public RationalLarge divide(RationalLarge other) {
        return MathUtil.reduce(other.num * this.den, other.den * this.num);
    }

    public RationalLarge divide(Rational other) {
        return MathUtil.reduce(other.num * this.den, other.den * this.num);
    }

    public RationalLarge divideBy(RationalLarge other) {
        return MathUtil.reduce(this.num * other.den, this.den * other.num);
    }

    public RationalLarge divideBy(Rational other) {
        return MathUtil.reduce(this.num * other.den, this.den * other.num);
    }

    public double scalar() {
        return this.num / this.den;
    }

    public long scalarClip() {
        return this.num / this.den;
    }
}
