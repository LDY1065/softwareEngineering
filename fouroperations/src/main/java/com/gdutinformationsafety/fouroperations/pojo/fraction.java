package com.gdutinformationsafety.fouroperations.pojo;

public class fraction {
    //分子
    private long numerator;
    //分母
    private long denominator;

    public fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public fraction(String str){
        int i=str.indexOf("/");
        this.numerator=Long.valueOf(str.substring(0,i));
        this.denominator=Long.valueOf(str.substring(i+1));
    }

    /**
     * @Author huzhidong
     * @Description //最大公约数
     * @Date 17:49 2018/12/25
     * @Param []
     * @return long
     **/
    public long getGcd() {
        long gcd = 0L;
        long tempNum = numerator > 0 ? numerator : -numerator;
        long tempDen = denominator > 0 ? denominator : -denominator;
        if (tempNum < tempDen) {// 交换n1、n2的值
            tempNum = tempNum + tempDen;
            tempDen = tempNum - tempDen;
            tempNum = tempNum - tempDen;
        }
        if (tempNum % tempDen == 0) {
            gcd = tempDen;
        }
        while (tempNum % tempDen > 0) {
            tempNum = tempNum % tempDen;

            if (tempNum < tempDen) {
                tempNum = tempNum + tempDen;
                tempDen = tempNum - tempDen;
                tempNum = tempNum - tempDen;
            }
            if (tempNum % tempDen == 0) {
                gcd = tempDen;
            }
        }
        return gcd;
    }
    /**
     * @Author huzhidong
     * @Description //最小公倍数
     * @Date 17:49 2018/12/25
     * @Param []
     * @return long
     **/
    public long getLcm() {
        long tempNum = numerator > 0 ? numerator : -numerator;
        long tempDen = denominator > 0 ? denominator : -denominator;
        return tempNum * tempDen / getGcd();
    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }

        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) {
            return false;
        }

        // 类型相同, 比较内容是否相同
        fraction other = (fraction) obj;
        fraction thisBast = this.getBast();
        fraction otherBast = other.getBast();

        return thisBast.getBastString().equals(otherBast.getBastString());
    }

    /***
     * @Author huzhidong
     * @Description //获取最简单表达
     * @Date 17:41 2018/12/25
     * @Param []
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction getBast(){
        if(this.getNumerator()==0){
            return this;
        }
        long gcd = this.getGcd();
        long numerator = this.getNumerator() / gcd;
        long denominator = this.getDenominator() / gcd;
        if (numerator < 0 && denominator < 0) {
            return new fraction(-numerator,-denominator);
        }
        return new fraction(numerator,denominator);
    }

    /***
     * @Author huzhidong
     * @Description //获取最简单表达
     * @Date 17:41 2018/12/25
     * @Param []
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public String getBastString(){
        fraction bast = this.getBast();
        long numerator = bast.getNumerator();
        long denominator = bast.getDenominator();
        if (numerator < 0 || denominator < 0) {
            fraction absolute = bast.absolute();
            return "-"+absolute.getNumerator()+"/"+absolute.getDenominator();
        }
        if(numerator==0){
            return "0";
        }
        if(numerator>=denominator){
            int i=0;
            do{
                numerator-=denominator;
                i++;
            }while (numerator>=denominator);
            if(numerator==0){
                return ""+i;
            }else{
                return i+"'"+numerator+"/"+denominator;
            }
        }
        return numerator+"/"+denominator;
    }

    /***
     * @Author huzhidong
     * @Description //比较当前与other ,小于返回-1 等于返回0 大于返回1
     * @Date 17:32 2018/12/25
     * @Param [fraction]
     * @return int
     **/
    public int compare(fraction other) {
        if (other == null) {
            new Exception("error:fration is not allow null");
        }
        boolean equals = this.equals(other);
        if (equals) {
            return 0;
        }
        fraction thisBast = this.getBast();
        fraction otherBast = other.getBast();
        long thisBastNumerator = thisBast.getNumerator();
        long thisBastDenominator = thisBast.getDenominator();
        long otherBastNumerator = otherBast.getNumerator();
        long otherBastDenominator = otherBast.getDenominator();
        //是否含有负数
        boolean thisHaveRev = false;
        boolean otherHaveRev = false;
        if (thisBastNumerator < 0 || thisBastDenominator < 0) {
            thisHaveRev = true;
        }
        if (otherBastNumerator < 0 || otherBastDenominator < 0) {
            otherHaveRev = true;
        }
        if (thisHaveRev && !otherHaveRev) {
            return -1;
        }
        if (!thisHaveRev && otherHaveRev) {
            return 1;
        }

        return thisBastNumerator * otherBastDenominator > otherBastNumerator * thisBastDenominator ? 1:-1;
    }

    /**
     * @Author huzhidong
     * @Description //判断是否在范围 在true 不在false
     * @Date 18:30 2018/12/25
     * @Param [fraction1, fraction2, type] type -1:左包含 1:右包含 0:左右都包含 2:左右都不包含
     * @return boolean
     **/
    public boolean inRange(fraction fraction1, fraction fraction2, fraction.ContainType type){
        if (fraction1 == null || fraction2 == null) {
            return false;
        }
        int compare = fraction1.compare(fraction2);
        //fraction1 大于 fraction2
        if (compare == 1) {
            fraction temp = fraction1;
            fraction1 = fraction2;
            fraction2 = temp;
        }
        int compare1 = this.compare(fraction1);
        int compare2 = this.compare(fraction2);
        switch (type){
            case left:
                if (compare1 >= 0 && compare2 == -1) {
                    return true;
                }
                break;
            case both:
                if (compare1 >= 0 && compare2 <= 0) {
                    return true;
                }
                break;
            case right:
                if (compare1 == 1 && compare2 <= 0) {
                    return true;
                }
                break;
            case unboth:
                if (compare1 == 1 && compare2 == -1) {
                    return true;
                }
                break;
            default:

                break;
        }
        return false;
    }

    /**
     * @Author huzhidong
     * @Description //加法
     * @Date 11:37 2018/12/26
     * @Param [fraction]
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction add(fraction fraction) {
        if (fraction == null) {
            return null;
        }
        com.gdutinformationsafety.fouroperations.pojo.fraction otherBast = fraction.getBast();
        com.gdutinformationsafety.fouroperations.pojo.fraction thisBast = this.getBast();
        long otherNumerator = otherBast.getNumerator();
        long otherDenominator = otherBast.getDenominator();
        long thisNumerator = thisBast.getNumerator();
        long thisDenominator = thisBast.getDenominator();
        long tempNumerator = otherNumerator * thisDenominator + thisNumerator * otherDenominator;
        long tempDenominator = thisDenominator * otherDenominator;
        return new fraction(tempNumerator,tempDenominator).getBast();
    }

    /**
     * @Author huzhidong
     * @Description //减法
     * @Date 11:43 2018/12/26
     * @Param [sub]
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction subtract(fraction sub) {
        if (sub == null) {
            return null;
        }
        fraction otherBast = sub.getBast();
        fraction thisBast = this.getBast();
        long otherNumerator = otherBast.getNumerator();
        long otherDenominator = otherBast.getDenominator();
        long thisNumerator = thisBast.getNumerator();
        long thisDenominator = thisBast.getDenominator();
        long tempNumerator = thisNumerator * otherDenominator - otherNumerator * thisDenominator;
        long tempDenominator = thisDenominator * otherDenominator;
        return new fraction(tempNumerator,tempDenominator).getBast();
    }

    /**
     * @Author huzhidong
     * @Description //乘法
     * @Date 11:48 2018/12/26
     * @Param [mul]
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction multiply(fraction mul) {
        if (mul == null) {
            return null;
        }
        fraction otherBast = mul.getBast();
        fraction thisBast = this.getBast();
        long otherNumerator = otherBast.getNumerator();
        long otherDenominator = otherBast.getDenominator();
        long thisNumerator = thisBast.getNumerator();
        long thisDenominator = thisBast.getDenominator();
        long tempNumerator = thisNumerator * otherNumerator;
        long tempDenominator = thisDenominator * otherDenominator;
        return new fraction(tempNumerator,tempDenominator).getBast();
    }

    /**
     * @Author huzhidong
     * @Description //除法
     * @Date 11:49 2018/12/26
     * @Param [div]
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction divide(fraction div) {
        if (div == null) {
            return null;
        }
        fraction otherBast = div.getBast();
        fraction thisBast = this.getBast();
        long otherNumerator = otherBast.getNumerator();
        long otherDenominator = otherBast.getDenominator();
        long thisNumerator = thisBast.getNumerator();
        long thisDenominator = thisBast.getDenominator();
        long tempNumerator = thisNumerator * otherDenominator;
        long tempDenominator = thisDenominator * otherNumerator;
        return new fraction(tempNumerator,tempDenominator).getBast();
    }

    /**
     * @Author huzhidong
     * @Description //绝对值获取
     * @Date 11:57 2018/12/26
     * @Param []
     * @return com.zdhu.testmybatiesplus.entity.Fraction
     **/
    public fraction absolute() {
        long numerator = this.getNumerator() < 0 ? -this.getNumerator() : this.getNumerator();
        long denominator = this.getDenominator() < 0 ? -this.getDenominator() : this.getDenominator();
        return new fraction(numerator,denominator).getBast();
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    @Override
    public String toString() {
        return "Fraction{" +
                "numerator=" + numerator +
                ", denominator=" + denominator +
                '}';
    }

    public static enum ContainType{
        left(-1,"左包含"),
        right(1,"右包含"),
        both(0,"左右包含"),
        unboth(2,"左右不包含");
        private int type;
        private String desc;

        ContainType(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }



}
