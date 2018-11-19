package com.bisa.health.ecg.encoder;

public class ECGBuildAnasysHelperBack {

    private int[] EXC_DATA = new int[1000];
    private final int AVG_LENGTH = 5;
    private int[] AVG_ECG;


    public ECGBuildAnasysHelperBack() {
        super();

    }

    public void aDatainit() {
        EXC_DATA[0] = 708124191;
        EXC_DATA[1] = 1607;
        EXC_DATA[2] = 542591052;
        EXC_DATA[3] = 542197593;
        EXC_DATA[4] = 538314520;
        EXC_DATA[5] = 150;
        EXC_DATA[6] = 250;
        EXC_DATA[7] = 750;

        EXC_DATA[43] = 0;
        EXC_DATA[46] = 0;
        EXC_DATA[90] = 0;
        EXC_DATA[91] = 0;
        EXC_DATA[92] = 0;
        EXC_DATA[93] = 0;
        EXC_DATA[94] = 0;

        for (int i = 10; i < EXC_DATA.length; i++) {
            EXC_DATA[i] = 0;
        }

        AVG_ECG = new int[AVG_LENGTH];
        for (int i = 0; i < AVG_ECG.length; i++) {
            AVG_ECG[i] = 70;
        }

    }

    public int[] ecgDataAnasys(int PECG_DATA) {

        int PECG_DATA6, kkk, JPQ0;
        EXC_DATA[12] = 0;
        EXC_DATA[13] = EXC_DATA[13] + 1;
        if (EXC_DATA[48] > 5) {
            kkk = EXC_DATA[49] - 5;
            if (kkk < 100)
                kkk = kkk + 100;
            if (kkk > 999)
                kkk = 100;
            if (Math.abs(EXC_DATA[kkk] - PECG_DATA) < 2) {
                kkk = EXC_DATA[49] - 3;
                if (kkk < 100)
                    kkk = kkk + 100;
                if (kkk > 999)
                    kkk = 100;
                EXC_DATA[15] = kkk;

            }

        }

        if ((PECG_DATA > 850) || (PECG_DATA < 150)) {
            EXC_DATA[18] = 0;
            EXC_DATA[23] = EXC_DATA[23] + 1;
        }
        PECG_DATA6 = PECG_DATA - EXC_DATA[10];
        EXC_DATA[21] = EXC_DATA[21] + 1;
        if (EXC_DATA[48] > 100) {

            switch (EXC_DATA[18]) {
                case 0:
                    EXC_DATA[19] = PECG_DATA;
                    EXC_DATA[20] = PECG_DATA;
                    EXC_DATA[22] = EXC_DATA[49];
                    EXC_DATA[24] = EXC_DATA[49];
                    EXC_DATA[13] = 0;
                    EXC_DATA[18] = 1;
                    break;
                case 1:
                    if (EXC_DATA[19] < PECG_DATA) {
                        EXC_DATA[19] = PECG_DATA;
                        EXC_DATA[22] = EXC_DATA[49];
                    }
                    if (EXC_DATA[20] > PECG_DATA) {
                        EXC_DATA[20] = PECG_DATA;
                        EXC_DATA[24] = EXC_DATA[49];

                    }

                    if ((EXC_DATA[19] - EXC_DATA[20]) > 32) {
                        EXC_DATA[25] = EXC_DATA[24] - EXC_DATA[22];
                        if ((EXC_DATA[25] > 0) & (EXC_DATA[25] < 30)) {
                            EXC_DATA[18] = 2;
                        } else {
                            EXC_DATA[18] = 0;
                        }
                    }
                    break;
                case 2:
                    if (EXC_DATA[20] > PECG_DATA)
                        EXC_DATA[20] = PECG_DATA;

                    if (PECG_DATA6 > -1)
                        EXC_DATA[18] = 3;

                    EXC_DATA[47] = Math.abs(EXC_DATA[EXC_DATA[15]] - PECG_DATA);

                    break;
                case 3:
                    if (EXC_DATA[20] > PECG_DATA)
                        EXC_DATA[20] = PECG_DATA;

                    kkk = Math.abs(EXC_DATA[EXC_DATA[15]] - PECG_DATA);
                    if (kkk > (EXC_DATA[47] - 5)) {
                        EXC_DATA[47] = kkk;
                    } else {
                        EXC_DATA[25] = EXC_DATA[49] - EXC_DATA[22];
                        if (EXC_DATA[49] < EXC_DATA[22])
                            EXC_DATA[25] = 900 + EXC_DATA[49] - EXC_DATA[22];

                        if (EXC_DATA[25] > 33)
                            EXC_DATA[18] = 0;
                        else {
                            EXC_DATA[28] = EXC_DATA[22] - EXC_DATA[15];
                            if (EXC_DATA[22] < EXC_DATA[15])
                                EXC_DATA[28] = 900 + EXC_DATA[22] - EXC_DATA[15];

                            EXC_DATA[28] = EXC_DATA[22] - EXC_DATA[15];
                            EXC_DATA[18] = 4;
                            EXC_DATA[22] = EXC_DATA[25];
                            EXC_DATA[29] = EXC_DATA[28] + EXC_DATA[22];

                        }
                    }
                    break;
                case 4:
                    EXC_DATA[29] = EXC_DATA[29] + 1;
                    EXC_DATA[25] = EXC_DATA[25] + 1;
                    if (EXC_DATA[25] > 85) {
                        if (PECG_DATA6 > -1) {
                            EXC_DATA[30] = EXC_DATA[29];
                            EXC_DATA[18] = 0;
                            EXC_DATA[27] = EXC_DATA[22];
                            if ((EXC_DATA[28] + EXC_DATA[22]) < 80) {
                                EXC_DATA[14] = EXC_DATA[13];
                                EXC_DATA[43] = EXC_DATA[21] - EXC_DATA[25] + EXC_DATA[26];
                                EXC_DATA[21] = 0;

                                int heart = (250 * 60) / EXC_DATA[43];
                                int[] _AVG_ECG = new int[AVG_ECG.length];
                                _AVG_ECG[0] = heart;
                                for (int i = 0, j = 1; i < AVG_ECG.length - 1; i++, j++) {
                                    _AVG_ECG[j] = AVG_ECG[i];
                                }
                                AVG_ECG = _AVG_ECG;
                                int AVG_VAL = (AVG_ECG[0] + AVG_ECG[1] + AVG_ECG[2] + AVG_ECG[3] + AVG_ECG[4]) / 5;
                                EXC_DATA[93] = AVG_VAL;

                                if (EXC_DATA[23] < 1) {
                                    EXC_DATA[34] = EXC_DATA[43];
                                    EXC_DATA[90] = EXC_DATA[90] + 1;
                                    EXC_DATA[46] = EXC_DATA[43] + EXC_DATA[46]; // R-R

                                    if (EXC_DATA[44] < 100) {
                                        EXC_DATA[44] = EXC_DATA[44] + 1;
                                    }
                                    if (EXC_DATA[43] > EXC_DATA[6]) {
                                        if (EXC_DATA[44] > 2) {
                                            EXC_DATA[91] = EXC_DATA[91] + 1;
                                        }
                                    }
                                    if (EXC_DATA[43] < EXC_DATA[5]) {
                                        if (EXC_DATA[44] > 2) {
                                            EXC_DATA[92] = EXC_DATA[92] + 1;
                                        }
                                    }

                                    if (EXC_DATA[43] > EXC_DATA[7]) {
                                        if (EXC_DATA[44] > 2) {
                                            EXC_DATA[94] = EXC_DATA[94] + 1;
                                        }
                                    }
                                } else {
                                    EXC_DATA[44] = 0;
                                }

                                EXC_DATA[33] = EXC_DATA[43];
                                EXC_DATA[26] = EXC_DATA[25];
                                EXC_DATA[21] = 0;
                                EXC_DATA[18] = 0;
                                EXC_DATA[23] = 0;
                                EXC_DATA[24] = 1;
                            }

                        }
                    }
                    break;
            }
        }
        EXC_DATA[10] = PECG_DATA;
        EXC_DATA[48] = EXC_DATA[48] + 1;
        EXC_DATA[49] = EXC_DATA[49] + 1;
        if (EXC_DATA[49] < 100) {
            EXC_DATA[49] = 100;
        }
        if (EXC_DATA[49] > 999) {
            EXC_DATA[49] = 100;
        }
        EXC_DATA[EXC_DATA[49]] = PECG_DATA;
        return EXC_DATA;
    }


}
