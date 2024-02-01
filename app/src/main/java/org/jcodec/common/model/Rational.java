package org.jcodec.common.model;

import org.jcodec.common.StringUtils;
import org.jcodec.common.tools.MathUtil;

/* loaded from: classes.dex */
public class Rational {
    final int den;
    final int num;
    public static final Rational ONE = new Rational(1, 1);
    public static final Rational HALF = new Rational(1, 2);
    public static final Rational ZERO = new Rational(0, 1);

    /* renamed from: R */
    public static Rational m2132R(int num, int den) {
        return new Rational(num, den);
    }

    /* renamed from: R */
    public static Rational m2131R(int num) {
        return m2132R(num, 1);
    }

    public Rational(int num, int den) {
        this.num = num;
        this.den = den;
    }

    public int getNum() {
        return this.num;
    }

    public int getDen() {
        return this.den;
    }

    public static Rational parse(String string) {
        String[] split = StringUtils.split(string, ":");
        return new Rational(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public int hashCode() {
        int result = this.den + 31;
        return (result * 31) + this.num;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Rational other = (Rational) obj;
            return this.den == other.den && this.num == other.num;
        }
        return false;
    }

    public int multiplyS(int val) {
        return (int) ((this.num * val) / this.den);
    }

    public int divideS(int val) {
        return (int) ((this.den * val) / this.num);
    }

    public int divideByS(int val) {
        return this.num / (this.den * val);
    }

    public long multiply(long val) {
        return (this.num * val) / this.den;
    }

    public long divide(long val) {
        return (this.den * val) / this.num;
    }

    public Rational flip() {
        return new Rational(this.den, this.num);
    }

    public boolean smallerThen(Rational sec) {
        return this.num * sec.den < sec.num * this.den;
    }

    public boolean greaterThen(Rational sec) {
        return this.num * sec.den > sec.num * this.den;
    }

    public boolean smallerOrEqualTo(Rational sec) {
        return this.num * sec.den <= sec.num * this.den;
    }

    public boolean greaterOrEqualTo(Rational sec) {
        return this.num * sec.den >= sec.num * this.den;
    }

    public boolean equals(Rational other) {
        return this.num * other.den == other.num * this.den;
    }

    public Rational plus(Rational other) {
        return MathUtil.reduce((this.num * other.den) + (other.num * this.den), this.den * other.den);
    }

    public RationalLarge plus(RationalLarge other) {
        return MathUtil.reduce((this.num * other.den) + (other.num * this.den), this.den * other.den);
    }

    public Rational minus(Rational other) {
        return MathUtil.reduce((this.num * other.den) - (other.num * this.den), this.den * other.den);
    }

    public RationalLarge minus(RationalLarge other) {
        return MathUtil.reduce((this.num * other.den) - (other.num * this.den), this.den * other.den);
    }

    public Rational plus(int scalar) {
        return new Rational(this.num + (this.den * scalar), this.den);
    }

    public Rational minus(int scalar) {
        return new Rational(this.num - (this.den * scalar), this.den);
    }

    public Rational multiply(int scalar) {
        return new Rational(this.num * scalar, this.den);
    }

    public Rational divide(int scalar) {
        return new Rational(this.den * scalar, this.num);
    }

    public Rational divideBy(int scalar) {
        return new Rational(this.num, this.den * scalar);
    }

    public Rational multiply(Rational other) {
        return MathUtil.reduce(this.num * other.num, this.den * other.den);
    }

    public RationalLarge multiply(RationalLarge other) {
        return MathUtil.reduce(this.num * other.num, this.den * other.den);
    }

    public Rational divide(Rational other) {
        return MathUtil.reduce(other.num * this.den, other.den * this.num);
    }

    public RationalLarge divide(RationalLarge other) {
        return MathUtil.reduce(other.num * this.den, other.den * this.num);
    }

    public Rational divideBy(Rational other) {
        return MathUtil.reduce(this.num * other.den, this.den * other.num);
    }

    public RationalLarge divideBy(RationalLarge other) {
        return MathUtil.reduce(this.num * other.den, this.den * other.num);
    }

    public float scalar() {
        return this.num / this.den;
    }

    public int scalarClip() {
        return this.num / this.den;
    }
}
