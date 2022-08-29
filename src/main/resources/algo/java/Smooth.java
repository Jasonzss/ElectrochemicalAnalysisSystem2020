package com.bluedot.queue;

/**
 * <p>
 * Title: smooth
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @param
 * @return
 */

public class Smooth {
    public static double[] smooth(double[] d) {

        int length = d.length;
        double[] dbRt = new double[length];

        if (length == 1) {
/** 前置与后置无元素 index=0与index=length-1 */
            dbRt[0] = d[0];
            dbRt[length - 1] = d[length - 1];

        }
        if (length == 2) {
/** 前置与后置无元素 index=0与index=length-1 */
            dbRt[0] = d[0];
            dbRt[length - 1] = d[length - 1];
        }
        if (length == 3) {
/** 前置与后置无元素 index=0与index=length-1 */
            dbRt[0] = d[0];
            dbRt[length - 1] = d[length - 1];
/** 前置与后置只有一个元素 index=1 与index=length-2 */
            dbRt[1] = (d[0] + d[1] + d[2]) / 3;

        }
        if (length == 4) {
/** 前置与后置无元素 index=0与index=length-1 */
            dbRt[0] = d[0];
            dbRt[length - 1] = d[length - 1];
/** 前置与后置只有一个元素 index=1 与index=length-2 */
            dbRt[1] = (d[0] + d[1] + d[2]) / 3;
            dbRt[2] = (d[1] + d[2] + d[3]) / 3;
        }
        if (length >= 5) {

/** 前置与后置无元素 index=0与index=length-1 */
            dbRt[0] = d[0];
/** 前置与后置只有一个元素 index=1 与index=length-2 */
            dbRt[1] = (d[0] + d[1] + d[2]) / 3;
            for (int x = 2; x < length - 2; x++) {
/** 前置与后置均由两个元素情况 d[n] n-2>=0且n+2<=length-1 */
                dbRt[x] = (d[x - 2] + d[x - 1] + d[x] + d[x + 1] + d[x + 2]) / 5;
            }
            dbRt[length - 2] = (d[length - 3] + d[length - 2] + d[length - 1]) / 3;
            dbRt[length - 1] = d[length - 1];

        }

        return dbRt;

    }

    public static void main(String[] args) {
        double[] dbPara = {1, 2, 100, 10, 50, 20};
        double[] dbRt = smooth(dbPara);
        for (double dbIndex : dbRt) {
            System.out.println(dbIndex);
        }

    }
}