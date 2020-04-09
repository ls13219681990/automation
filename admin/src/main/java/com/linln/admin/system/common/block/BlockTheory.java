package com.linln.admin.system.common.block;

import java.util.ArrayList;
import java.util.List;

import static com.linln.admin.system.common.CommonAlgorithm.*;


/**
 * 块体理论计算
 */
public class BlockTheory {


    public static List<double[]> blockCalculate(double[][] P_Value,int Axis_azimuth) {

        //int P_Num, Axis_azimuth; //P_Num输入节理数量 Axis_azimuth轴线方位角
        int C_P_Count;
        double[][] SSSC_Value, DSSC_Value, EV_Value, TV_SP_Relation, IV_SP_Relation, Rp, C_Angle, C_Angle_Area;
        //P_Value,
        // P_Value 输入结构面参数 倾角、倾向、内摩擦角--->结构面参数统计表倾角、走向、内摩擦角正切值
        //SSSC_Value各结构面法向量nk及单滑稳定系数ηi,
        //DSSC_Value沿Gij双面滑动稳定系数ηij,
        //EV_Value棱向向量Gij统计表
        //TV_SP_Relation倾向向量Gi与各结构面的相对位置关系表,
        //IV_SP_Relation各交线向量Gij与-Gij同各结构面的相对关系
        //Rp,棱线向量Rp
        //C_Angle_Area //所求角度范围
        //C_Angle 对应Gij的起始角度


        List<double[][]> returnList = new ArrayList<>();


        //P_Num = dataGridView1.RowCount - 1;
        int P_Num = P_Value.length;

        //P_Value = new double[P_Num][3];

        SSSC_Value = new double[P_Num][7];

        Structural_Plane_Parameter_Statistics(P_Num, P_Value); //结构面参数统计表

        Single_slip_stability_coefficient(P_Num, P_Value, SSSC_Value); //各结构面nk及单滑稳定系数ηi
        C_P_Count = 0;
        //表三行数
        for (int i = 0; i < P_Num; i++) {
            if (P_Num - i - 1 >= 0) {
                C_P_Count = C_P_Count + P_Num - i - 1;
            }
        }
        DSSC_Value = new double[C_P_Count][6];

        EV_Value = new double[C_P_Count * 2][4]; //Edge vector 棱向向量

        Double_slip_stability_coefficient(P_Num, C_P_Count, SSSC_Value, P_Value, DSSC_Value, EV_Value); //各结构面nk及单滑稳定系数ηi,棱向向量
        //Trend vector  倾向向量  Structural Planes 结构面 Intersection vector 交线向量
        TV_SP_Relation = new double[P_Num][P_Num];
        Trend_vector_Structural_Planes_Relation(P_Num, SSSC_Value, TV_SP_Relation);
        IV_SP_Relation = new double[P_Num][C_P_Count * 2];
        Intersection_vector_Structural_Planes_Relation(P_Num, C_P_Count, SSSC_Value, EV_Value, IV_SP_Relation);
        Rp = new double[C_P_Count * 2][P_Num + 1];
        Rp_Compute(P_Num, C_P_Count, TV_SP_Relation, IV_SP_Relation, Rp);

        C_Angle = new double[C_P_Count * 2][2];

        Compute_Angle(Axis_azimuth, C_P_Count, EV_Value, C_Angle);
        C_Angle_Area = new double[P_Num + 1][2];
        Compute_Angle_Area(P_Num, C_P_Count, Rp, C_Angle, C_Angle_Area);


List<double[]> returnValue = new ArrayList<>();

        //遍历二维数组中每一个一维数组
        for(double[] cells : C_Angle_Area) {
           for (int i = 0;i<cells.length;i++){
               cells[i] = Double.valueOf( String.format("%.4f", cells[i]));
           }
            returnValue.add(cells);
        }
        /*returnList.add(P_Value);
        returnList.add(SSSC_Value);
        returnList.add(DSSC_Value);
        returnList.add(EV_Value);
        returnList.add(TV_SP_Relation);
        returnList.add(IV_SP_Relation);
        returnList.add(Rp);
        returnList.add(C_Angle);*/
        //C_Angle_Area //所求角度范围
        //C_Angle 对应Gij的起始角度
        //returnList.add(C_Angle_Area);
        return returnValue;
    }


    public static void main(String[] args) {





        double[][] value = {{67d, 170d,45d}, {65d, 48d, 45d},{65d, 325d,45d}, {88d, 266d, 45d}};

        //double[][] value = {{90d, 40d,45d}, {67d, 80d, 45d}, {81d, 90d, 45d},{48d, 245d, 45d},{81d, 160d, 45d}};
        int  Axis_azimuth = 178;





        System.out.println(blockCalculate(value,Axis_azimuth));
    }

}









