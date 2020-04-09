package com.linln.admin.system.common;

/**
 * 公共算法
 */
public class CommonAlgorithm {

    //2：各结构面nk及单滑稳定系数ηi
    public static void Single_slip_stability_coefficient(int P_Num, double[][] P_Value, double[][] SSSC_Value) {
        for (int i = 0; i < P_Num; i++) {

            SSSC_Value[i][0] = Math.sin(P_Value[i][0] * Math.PI / 180) * Math.sin(P_Value[i][1] * Math.PI / 180);
            SSSC_Value[i][1] = -Math.sin(P_Value[i][0] * Math.PI / 180) * Math.cos(P_Value[i][1] * Math.PI / 180);
            SSSC_Value[i][2] = Math.cos(P_Value[i][0] * Math.PI / 180);
            SSSC_Value[i][3] = Math.cos(P_Value[i][0] * Math.PI / 180) * Math.sin(P_Value[i][1] * Math.PI / 180);
            SSSC_Value[i][4] = -Math.cos(P_Value[i][0] * Math.PI / 180) * Math.cos(P_Value[i][1] * Math.PI / 180);
            SSSC_Value[i][5] = -Math.sin(P_Value[i][0] * Math.PI / 180);
            SSSC_Value[i][6] = 1 / Math.tan(P_Value[i][0] * Math.PI / 180) * Math.tan(P_Value[i][2] * Math.PI / 180);

        }
    }

    // 1 结构面参数统计表
    public static void Structural_Plane_Parameter_Statistics(int P_Num, double[][] P_Value) //
    {


        for (int i = 0; i < P_Num; i++) {
            for (int j = 0; j < 3; j++) {

                if (j == 1) {
                    P_Value[i][j] = (P_Value[i][j] + 90) % 360;
                }

                /*dataGridView2.Rows[i].Cells[j].Value = P_Value[i, j].ToString();
                if (j == 2)
                    dataGridView2.Rows[i].Cells[j].Value = Math.Tan(P_Value[i, j] * Math.PI / 180).ToString();*/
            }
        }


    }
    //3.4沿Gij双面滑动稳定系数ηij,棱向向量
    public static void Double_slip_stability_coefficient(int P_Num, int C_P_Count, double[][] SSSC_Value, double[][] P_Value, double[][] DSSC_Value, double[][] EV_Value)
    {
        int D_Index = 0;
		/*dataGridView4.RowCount = C_P_Count;
		dataGridView5.RowCount = C_P_Count;*/
        for (int i = 0; i < P_Num; i++) {
            for (int j = i + 1; j < P_Num; j++) {

                DSSC_Value[D_Index][0] = j + 1;
                DSSC_Value[D_Index][0] = i + 1 + DSSC_Value[D_Index][0] / 10000;

                DSSC_Value[D_Index][1] = SSSC_Value[i][1] * SSSC_Value[j][2] - SSSC_Value[j][1] * SSSC_Value[i][2];
                DSSC_Value[D_Index][2] = SSSC_Value[j][0] * SSSC_Value[i][2] - SSSC_Value[i][0] * SSSC_Value[j][2];
                DSSC_Value[D_Index][3] = SSSC_Value[i][0] * SSSC_Value[j][1] - SSSC_Value[j][0] * SSSC_Value[i][1];
                DSSC_Value[D_Index][4] = DSSC_Value[D_Index][1] * DSSC_Value[D_Index][1] + DSSC_Value[D_Index][2] * DSSC_Value[D_Index][2] + DSSC_Value[D_Index][3] * DSSC_Value[D_Index][3];
                DSSC_Value[D_Index][5] = DSSC_Value[D_Index][2] * SSSC_Value[j][0] - DSSC_Value[D_Index][1] * SSSC_Value[j][1] + DSSC_Value[D_Index][1] * SSSC_Value[i][1] - DSSC_Value[D_Index][2] * SSSC_Value[i][0];
                DSSC_Value[D_Index][5] = DSSC_Value[D_Index][5] * Math.tan(P_Value[i][2] * Math.PI / 180) / DSSC_Value[D_Index][3];
                DSSC_Value[D_Index][5] = DSSC_Value[D_Index][5] / Math.sqrt(DSSC_Value[D_Index][4]);
                // dataGridView4.Rows.Add();
				/*for (int k = 0; k< 6; k++)
				{
					dataGridView4.Rows[D_Index].Cells[k].setValue(String.format("%0.4f", DSSC_Value[D_Index][k]));
				}*/
                int signN = -1;
                if (DSSC_Value[D_Index][3] < 0) {
                    signN = 1;
                }
                double tempData;
                tempData = signN / Math.sqrt(DSSC_Value[D_Index][4]);
                EV_Value[D_Index][0] = DSSC_Value[D_Index][0];
                EV_Value[D_Index][1] = tempData * DSSC_Value[D_Index][1];
                EV_Value[D_Index][2] = tempData * DSSC_Value[D_Index][2];
                EV_Value[D_Index][3] = tempData * DSSC_Value[D_Index][3];
                EV_Value[D_Index + C_P_Count][0] = -EV_Value[D_Index][0];
                EV_Value[D_Index + C_P_Count][1] = -EV_Value[D_Index][1];
                EV_Value[D_Index + C_P_Count][2] = -EV_Value[D_Index][2];
                EV_Value[D_Index + C_P_Count][3] = -EV_Value[D_Index][3];
                //dataGridView5.Rows.Add();
				/*for (int k = 0; k < 4; k++)
				{
					dataGridView5.Rows[D_Index].Cells[k].setValue(String.format("%0.4f", EV_Value[D_Index][k]));
				}*/

                D_Index = D_Index + 1;
            }
        }



    }
    //5.倾向向量与各结构面关系
    public  static void Trend_vector_Structural_Planes_Relation ( int P_Num, double[][] SSSC_Value, double[][]
            TV_SP_Relation){

        for (int i = 0; i < P_Num; i++) {
            for (int j = 0; j < P_Num; j++) {
                TV_SP_Relation[i][j] = SSSC_Value[i][0] * SSSC_Value[j][3] + SSSC_Value[i][1] * SSSC_Value[j][4] + SSSC_Value[i][2] * SSSC_Value[j][5];
//C# TO JAVA CONVERTER TODO TASK: Math.Round can only be converted to Java's Math.round if just one argument is used:
                //TV_SP_Relation[i][j] = Math.round(TV_SP_Relation[i][j], 4);
                //TV_SP_Relation[i][j] = (double)Math.round(TV_SP_Relation[i][j]);

                //利用字符串格式化的方式实现四舍五入,保留1位小数
               /* String  = String.format("%.4f",TV_SP_Relation[i][j]);
                TV_SP_Relation[i][j] = new BigDecimal(roundingFour).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();*/

                TV_SP_Relation[i][j] = CommonMethod.roundingFour(TV_SP_Relation[i][j]);


                //dataGridView6.Rows.Add();
                if (TV_SP_Relation[i][j] > 0) {
                    TV_SP_Relation[i][j] = 1d;
                }

                if (TV_SP_Relation[i][j] < 0) {
                    TV_SP_Relation[i][j] = -1d;
                }
                if (TV_SP_Relation[i][j] == 0) {
                    TV_SP_Relation[i][j] = 0d;
                }
                //dataGridView6.Rows[i].Cells[j].setValue(TV_SP_Relation[i][j].toString());
            }
        }

    }

    //倾向向量与各结构面关系
    public static void Intersection_vector_Structural_Planes_Relation(int P_Num, int C_P_Count, double[][] SSSC_Value, double[][] EV_Value, double[][] IV_SP_Relation)
    {

        for (int i = 0; i < P_Num; i++)
        {
            for (int j = 0; j < C_P_Count; j++)
            {
                IV_SP_Relation[i][j] = SSSC_Value[i][0] * EV_Value[j][1] + SSSC_Value[i][1] *EV_Value[j][2] + SSSC_Value[i][2] *EV_Value[j][3];
//C# TO JAVA CONVERTER TODO TASK: Math.Round can only be converted to Java's Math.round if just one argument is used:
                //IV_SP_Relation[i][j] = Math.round(IV_SP_Relation[i][j]);
                /*String roundingFour = String.format("%.4f",IV_SP_Relation[i][j]);
                IV_SP_Relation[i][j] = new BigDecimal(roundingFour).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();*/

                IV_SP_Relation[i][j] = CommonMethod.roundingFour(IV_SP_Relation[i][j]);


                if (IV_SP_Relation[i][j] > 0)
                {
                    IV_SP_Relation[i][j]=1;
                }
                if (IV_SP_Relation[i][j] < 0)
                {
                    IV_SP_Relation[i][j]=-1;
                }
                if (IV_SP_Relation[i][j] == 0)
                {
                    IV_SP_Relation[i][j]=0;
                }
                IV_SP_Relation[i][j+ C_P_Count] = -IV_SP_Relation[i][j];
                //dataGridView7.Rows.Add();
                // dataGridView7.Rows[i].Cells[j].Value = IV_SP_Relation[i,j].ToString();
                //  dataGridView7.Rows[i].Cells[j + C_P_Count].Value = IV_SP_Relation[i, j + C_P_Count].ToString(); ;
            }
        }



    }



    //倾向向量与各结构面关系
    public static void Rp_Compute(int P_Num, int C_P_Count, double[][] TV_SP_Relation, double[][] IV_SP_Relation, double[][] Rp)
    {
        //dataGridView8.RowCount =  C_P_Count * 2;
        //dataGridView8.ColumnCount =P_Num+1;
        for (int i = 0; i < C_P_Count*2; i++)
        {
            //dataGridView8.Rows.Add();
            Rp[i][0] = 1;
            for (int k=0;k<P_Num;k++)
            {
                if (IV_SP_Relation[k][i] > 0)
                {
                    Rp[i][0] = -1;
                }
            }
            // dataGridView8.Rows[i].Cells[0].Value = Rp[i,0].ToString();
            for (int j = 0; j < P_Num; j++)
            {
                Rp[i][j+1] = 1;
                for (int k=0;k<P_Num;k++)
                {
                    double tempData;
                    tempData = TV_SP_Relation[k][j] * IV_SP_Relation[k][i];
                    if (tempData == -1)
                    {
                        Rp[i][j+1] = -1;
                    }
                    if ((tempData==0) && (IV_SP_Relation[k][i]==-1))
                    {
                        Rp[i][j+1] = -1;
                    }
                }
                //   dataGridView8.Rows[i].Cells[j+1].Value =Rp[i, j+1].ToString();
            }
        }
    }

    //计算Gij对应角度范围
    public static void Compute_Angle_Area(int P_Num, int C_P_Count, double[][] Rp, double[][] C_Angle, double[][] C_Angle_Area)
    {
        // dataGridView9.RowCount = P_Num + 1;
        // dataGridView9.ColumnCount = 2;
        for (int i = 0; i < P_Num+1; i++)
        {
            boolean IsFirst = true;
            C_Angle_Area[i][1] = 0;
            C_Angle_Area[i][0] = 0;
            for (int j=0;j<C_P_Count*2;j++)
            {
                if(Rp[j][i]==1)
                {
                    if (C_Angle[j][0] < 0)
                    {
                        C_Angle[j][0] = C_Angle[j][0] + 360;
                    }
                    if (IsFirst)
                    {
                        IsFirst = false;
                        C_Angle_Area[i][1] = C_Angle[j][1];

                        C_Angle_Area[i][0] = C_Angle[j][0];
                    }
                    else
                    {
                        double sweepAngle;

                        sweepAngle= C_Angle_Area[i][1] - C_Angle_Area[i][0];
                        if (sweepAngle < 0)
                        {
                            sweepAngle = C_Angle_Area[i][1] + 360 - C_Angle_Area[i][0];
                        }
                        double C_diff = C_Angle[j][1] - C_Angle_Area[i][1];
                        if (C_diff>= 0)
                        {
                            //if (C_diff + sweepAngle <= 180) 原区间
                            if ((C_diff+sweepAngle>=180) && (C_diff<180))
                            {
                                C_Angle_Area[i][0] = C_Angle[j][0];
                            }
                            if (C_diff>=180)
                            {
                                if(C_diff+sweepAngle<=360)
                                {

                                    C_Angle_Area[i][1] = 0;
                                    C_Angle_Area[i][0] = 0;
                                    j = C_P_Count * 2;
                                }
                                else
                                {
                                    C_Angle_Area[i][1] = C_Angle[j][1];
                                }
                            }
                        }
                        if (C_diff<0)
                        {
                            if (Math.abs(C_diff) < sweepAngle)
                            {
                                C_Angle_Area[i][1] = C_Angle[j][1];
                            }
                            if (Math.abs(C_diff)>=sweepAngle)
                            {
                                if (Math.abs(C_diff)+180<=360)
                                {
                                    C_Angle_Area[i][1] = 0;
                                    C_Angle_Area[i][0] = 0;
                                    j = C_P_Count * 2;

                                }
                                if (Math.abs(C_diff) + 180 > 360)
                                {
                                    if (((Math.abs(C_diff) + 180) % 360) < sweepAngle)
                                    {
                                        C_Angle_Area[i][0] = C_Angle[j][0];
                                    }
                                }

                            }

                        }


                    }


                }
            }
            //    dataGridView9.Rows[i].Cells[0].Value = C_Angle_Area[i, 0].ToString("F4");
            //   dataGridView9.Rows[i].Cells[1].Value = C_Angle_Area[i, 1].ToString("F4");

        }
    }


    public static void Compute_Angle(int Axis_azimuth, int C_P_Count, double[][] EV_Value, double[][] C_Angle) //计算Gij对应角度范围
    {
        for (int i = 0; i < C_P_Count*2; i++)
        {
            double A, B;
            A = -EV_Value[i][1] * Math.sin(Axis_azimuth * Math.PI / 180) + EV_Value[i][2] * Math.cos(Axis_azimuth * Math.PI / 180);
            B = EV_Value[i][3];
            double B_Angle;
            if (B > 0)
            {
                B_Angle = Math.acos(A / Math.sqrt(A * A + B * B)) * 180 / Math.PI;
            }
            else
            {
                B_Angle = 360-Math.acos(A / Math.sqrt(A * A + B * B)) * 180 / Math.PI;
            }
            C_Angle[i][0] = 180 - B_Angle;
            C_Angle[i][1] = 360 - B_Angle;


        }
    }


    public static void main(String[] args) {
        String substring = "BB4403C91FBA01472C5D51440000000000000000000033570000000000000000000000000000000000000000000000000000000000000000000000000000000000009B3C0000";
        if(substring.indexOf("BA01")!=-1){
            //包含
            substring = substring.replace("BA01", "BB");
            System.out.println("转换过后的值1====>"+substring);
            /*i = (integer * 2) + (i + 4)+2;*/
        }else if(substring.indexOf("BA00")!=-1){
            substring = substring.replace("BA00", "BA");
            System.out.println("转换过后的值2====>"+substring);
            /*i = (integer * 2) + (i + 4)+2;*/
        }



    }

}
