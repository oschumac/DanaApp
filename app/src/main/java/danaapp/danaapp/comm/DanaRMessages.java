package danaapp.danaapp.comm;

import danaapp.danaapp.DanaConnection;

import java.util.HashMap;

public class DanaRMessages {
    public static HashMap<Integer,DanaRMessage> sDanaRMessages;
    
    static {
        sDanaRMessages = new HashMap<Integer,DanaRMessage>();
        sDanaRMessages.put(Integer.valueOf(12289), new DanaRMessage("CMD_CONNECT"));
        sDanaRMessages.put(Integer.valueOf(12290), new DanaRMessage("CMD_DISCONNECT"));

        sDanaRMessages.put(Integer.valueOf(12545), new DanaRMessage("CMD_HISTORY_MEAL_INS"));
        sDanaRMessages.put(Integer.valueOf(12546), new DanaRMessage("CMD_HISTORY_DAY_INS"));
        sDanaRMessages.put(Integer.valueOf(12547), new DanaRMessage("CMD_HISTORY_AIR_SUB"));
        sDanaRMessages.put(Integer.valueOf(12548), new DanaRMessage("CMD_HISTORY_GLUCOSE"));
        sDanaRMessages.put(Integer.valueOf(12549), new DanaRMessage("CMD_HISTORY_ALARM"));
        sDanaRMessages.put(Integer.valueOf(12550), new DanaRMessage("CMD_HISTORY_ERROR"));
        sDanaRMessages.put(Integer.valueOf(12551), new DanaRMessage("CMD_HISTORY_CARBOHY"));
        sDanaRMessages.put(Integer.valueOf(12552), new DanaRMessage("CMD_HISTORY_REFILL"));
        sDanaRMessages.put(Integer.valueOf(12553), new DanaRMessage("CMD_HISTORY_SUSPEND"));
        sDanaRMessages.put(Integer.valueOf(12554), new DanaRMessage("CMD_HISTORY_BASAL_HOUR"));
        sDanaRMessages.put(Integer.valueOf(12555), new DanaRMessage("CMD_HISTORY_TB"));
        sDanaRMessages.put(Integer.valueOf(12785), new DanaRMessage("CMD_HISTORY_DONT_USED"));
        sDanaRMessages.put(Integer.valueOf(12786), new DanaRMessage("CMD_HISTORY_LAST_T_R"));
        sDanaRMessages.put(Integer.valueOf(12787), new DanaRMessage("CMD_HISTORY_LAST_T_S"));

        sDanaRMessages.put(Integer.valueOf(12801), new DanaRMessage("CMD_SETTING_V_MEAL_INS_I"));
        sDanaRMessages.put(Integer.valueOf(12802), new DanaRMessage("CMD_SETTING_V_Based_INS_I"));
        sDanaRMessages.put(Integer.valueOf(12803), new DanaRMessage("CMD_SETTING_V_MEAL_SETTING_I"));
        sDanaRMessages.put(Integer.valueOf(12804), new DanaRMessage("CMD_SETTING_V_CCC_I"));
        sDanaRMessages.put(Integer.valueOf(12805), new DanaRMessage("CMD_SETTING_V_MAX_VALUE_I"));
        sDanaRMessages.put(Integer.valueOf(12806), new DanaRMessage("CMD_SETTING_V_BASAL_PROFILE_ALL"));
        sDanaRMessages.put(Integer.valueOf(12807), new DanaRMessage("CMD_SETTING_V_SHIPPING_I"));
        sDanaRMessages.put(Integer.valueOf(12808), new DanaRMessage("CMD_SETTING_V_CLOGGIN_SENS_I"));
        sDanaRMessages.put(Integer.valueOf(12809), new DanaRMessage("CMD_SETTING_V_GLUCOSEandEASY"));
        sDanaRMessages.put(Integer.valueOf(12810), new DanaRMessage("CMD_SETTING_V_TIME_I"));
        sDanaRMessages.put(Integer.valueOf(12811), new DanaRMessage("CMD_SETTING_V_USER_OPTIONS"));
        sDanaRMessages.put(Integer.valueOf(12812), new DanaRMessage("CMD_SETTING_V_PROFILE_NUMBER"));
        sDanaRMessages.put(Integer.valueOf(12813), new DanaRMessage("CMD_SETTING_V_CIR_CF_VALUE"));

        sDanaRMessages.put(Integer.valueOf(13057), new DanaRMessage("CMD_SETTING_MEAL_INS_S"));
        sDanaRMessages.put(Integer.valueOf(13058), new DanaRMessage("CMD_SETTING_Based_INS_S"));
        sDanaRMessages.put(Integer.valueOf(13059), new DanaRMessage("CMD_SETTING_MEAL_SETTING_S"));
        sDanaRMessages.put(Integer.valueOf(13060), new DanaRMessage("CMD_SETTING_CCC_S"));
        sDanaRMessages.put(Integer.valueOf(13061), new DanaRMessage("CMD_SETTING_MAX_VALUE_S"));
        sDanaRMessages.put(Integer.valueOf(13062), new DanaRMessage("CMD_SETTING_BASAL_PROFILE_S"));
        sDanaRMessages.put(Integer.valueOf(13063), new DanaRMessage("CMD_SETTING_SHIPPING_S"));
        sDanaRMessages.put(Integer.valueOf(13064), new DanaRMessage("CMD_SETTING_CLOGGIN_SENS_S"));
        sDanaRMessages.put(Integer.valueOf(13065), new DanaRMessage("CMD_SETTING_GLUCOSEandEASY_S"));
        sDanaRMessages.put(Integer.valueOf(13066), new DanaRMessage("CMD_SETTING_TIME_S"));
        sDanaRMessages.put(Integer.valueOf(13067), new DanaRMessage("CMD_SETTING_USER_OPTIONS_S"));
        sDanaRMessages.put(Integer.valueOf(13068), new DanaRMessage("CMD_SETTING_PROFILE_NUMBER_S"));
        sDanaRMessages.put(Integer.valueOf(13069), new DanaRMessage("CMD_SETTING_CIR_CF_VALUE_S"));

        sDanaRMessages.put(Integer.valueOf(257), new DanaRMessage("CMD_MEALINS_STOP"));
        sDanaRMessages.put(Integer.valueOf(258), new DanaRMessage("CMD_MEALINS_START_DATA"));
        sDanaRMessages.put(Integer.valueOf(259), new DanaRMessage("CMD_MEALINS_START_NODATA"));
        sDanaRMessages.put(Integer.valueOf(260), new DanaRMessage("CMD_MEALINS_START_DATA_SPEED"));
        sDanaRMessages.put(Integer.valueOf(261), new DanaRMessage("CMD_MEALINS_START_NODATA_SPEED"));

        sDanaRMessages.put(Integer.valueOf(513), new DanaRMessage("CMD_PUMP_ACT_INS_VALUE"));
        sDanaRMessages.put(Integer.valueOf(514), new DanaRMessage("CMD_PUMP_THIS_REMAINDER_MEAL_INS"));
        sDanaRMessages.put(Integer.valueOf(515), new DanaRMessage("CMD_PUMP_BASE_SET"));
        sDanaRMessages.put(Integer.valueOf(516), new DanaRMessage("CMD_PUMP_CALCULATION_SETTING"));
        sDanaRMessages.put(Integer.valueOf(517), new DanaRMessage("CMD_PUMP_EXERCISE_MODE"));
        sDanaRMessages.put(Integer.valueOf(518), new DanaRMessage("CMD_PUMP_MEAL_INS_I"));

        sDanaRMessages.put(Integer.valueOf(519), new DanaRMessage("CMD_PUMP_EXPANS_INS_I"));
        sDanaRMessages.put(Integer.valueOf(520), new DanaRMessage("CMD_PUMP_EXPANS_INS_RQ"));

        sDanaRMessages.put(Integer.valueOf(521), new DanaRMessage("CMD_PUMP_DUAL_INS_RQ"));
        sDanaRMessages.put(Integer.valueOf(522), new MsgStatusBasic("CMD_PUMP_INITVIEW_I"));
        sDanaRMessages.put(Integer.valueOf(523), new MsgStatus("CMD_PUMP_STATUS"));
        sDanaRMessages.put(Integer.valueOf(524), new DanaRMessage("CMD_PUMP_CAR_N_CIR"));

        sDanaRMessages.put(Integer.valueOf(769), new MsgStatusTime_InitialConnection("CMD_PUMPINIT_TIME_INFO"));
        sDanaRMessages.put(Integer.valueOf(770), new DanaRMessage("CMD_PUMPINIT_BOLUS_INFO"));
        sDanaRMessages.put(Integer.valueOf(771), new MsgStatusBasic_InitialConnection("CMD_PUMPINIT_INIT_INFO"));
        sDanaRMessages.put(Integer.valueOf(772), new DanaRMessage("CMD_PUMPINIT_OPTION"));

        sDanaRMessages.put(Integer.valueOf(1025), new MsgTempBasalStart("CMD_PUMPSET_EXERCISE_S"));
        sDanaRMessages.put(Integer.valueOf(1026), new DanaRMessage("CMD_PUMPSET_HIS_S"));
        sDanaRMessages.put(Integer.valueOf(1027), new DanaRMessage("CMD_PUMPSET_EXERCISE_STOP"));

        sDanaRMessages.put(Integer.valueOf(1028), new DanaRMessage("CMD_PUMPSET_PAUSE"));
        sDanaRMessages.put(Integer.valueOf(1029), new DanaRMessage("CMD_PUMPSET_PAUSE_STOP"));

        sDanaRMessages.put(Integer.valueOf(1030), new DanaRMessage("CMD_PUMPSET_EXPANS_INS_STOP"));
        sDanaRMessages.put(Integer.valueOf(1031), new DanaRMessage("CMD_PUMPSET_EXPANS_INS_S"));

        sDanaRMessages.put(Integer.valueOf(1032), new DanaRMessage("CMD_PUMPSET_DUAL_S"));
        sDanaRMessages.put(Integer.valueOf(1033), new DanaRMessage("CMD_PUMPSET_EASY_OFF"));

        sDanaRMessages.put(Integer.valueOf(1281), new DanaRMessage("CMD_HISPAGE_MEAL_INS"));
        sDanaRMessages.put(Integer.valueOf(1282), new DanaRMessage("CMD_HISPAGE_DAY_INS"));
        sDanaRMessages.put(Integer.valueOf(1283), new DanaRMessage("CMD_HISPAGE_AIR_SUB"));
        sDanaRMessages.put(Integer.valueOf(1284), new DanaRMessage("CMD_HISPAGE_GLUCOSE"));
        sDanaRMessages.put(Integer.valueOf(1285), new DanaRMessage("CMD_HISPAGE_ALARM"));
        sDanaRMessages.put(Integer.valueOf(1286), new DanaRMessage("CMD_HISPAGE_ERROR"));
        sDanaRMessages.put(Integer.valueOf(1287), new DanaRMessage("CMD_HISPAGE_CARBOHY"));
        sDanaRMessages.put(Integer.valueOf(1288), new DanaRMessage("CMD_HISPAGE_REFILL"));
        sDanaRMessages.put(Integer.valueOf(1290), new DanaRMessage("CMD_HISPAGE_DAILTY_PRE_DATA"));
        sDanaRMessages.put(Integer.valueOf(1291), new DanaRMessage("CMD_HISPAGE_BOLUS_AVG"));
        sDanaRMessages.put(Integer.valueOf(1292), new DanaRMessage("CMD_HISPAGE_BASAL_RECORD"));
        sDanaRMessages.put(Integer.valueOf(1293), new DanaRMessage("CMD_HISPAGE_TB"));

        sDanaRMessages.put(Integer.valueOf(1537), new DanaRMessage("CMD_PUMPOWAY_SYSTEM_STATUS"));
        sDanaRMessages.put(Integer.valueOf(1538), new DanaRMessage("CMD_PUMPOWAY_GLUCOSE_ALARM"));
        sDanaRMessages.put(Integer.valueOf(1539), new DanaRMessage("CMD_PUMPOWAY_LOW_INSULIN_ALARM"));

        sDanaRMessages.put(Integer.valueOf(1793), new DanaRMessage("CMD_MSGRECEP_TAKE_SUGAR"));
        sDanaRMessages.put(Integer.valueOf(1794), new DanaRMessage("CMD_MSGRECEP_GO_TO_DOCTOR"));
        sDanaRMessages.put(Integer.valueOf(1795), new DanaRMessage("CMD_MSGRECEP_CALL_TO_CAREGIVER"));
        sDanaRMessages.put(Integer.valueOf(1796), new DanaRMessage("CMD_MSGRECEP_CHECK_GLUCOSE_AGAIN"));
        sDanaRMessages.put(Integer.valueOf(1797), new DanaRMessage("CMD_MSGRECEP_CALL_TO_HOME"));
        sDanaRMessages.put(Integer.valueOf(1798), new DanaRMessage("CMD_MSGRECEP_DO_DELIVER"));
        sDanaRMessages.put(Integer.valueOf(2049), new DanaRMessage("CMD_MSGSEND_YES_I_DO"));
        sDanaRMessages.put(Integer.valueOf(2050), new DanaRMessage("CMD_MSGSEND_NO_I_CANNOT"));
        sDanaRMessages.put(Integer.valueOf(2051), new DanaRMessage("CMD_MSGSEND_CALL_TO_ME_MOM"));
        sDanaRMessages.put(Integer.valueOf(2052), new DanaRMessage("CMD_MSGSEND_DO_NOT_INFUSE"));

        sDanaRMessages.put(Integer.valueOf(2305), new DanaRMessage("CMD_FILL_REFILL_COUNT"));
        sDanaRMessages.put(Integer.valueOf(2306), new DanaRMessage("CMD_FILL_PRIME_CHECK"));
        sDanaRMessages.put(Integer.valueOf(2307), new DanaRMessage("CMD_FILL_PRIME_END"));
        sDanaRMessages.put(Integer.valueOf(2308), new DanaRMessage("CMD_FILL_PRIME_STOP"));
        sDanaRMessages.put(Integer.valueOf(2310), new DanaRMessage("CMD_FILL_PRIME_PAUSE"));
        sDanaRMessages.put(Integer.valueOf(2311), new DanaRMessage("CMD_FILL_PRIME_RATE"));

        sDanaRMessages.put(Integer.valueOf(16882), new DanaRMessage("CMD_HISTORY_ALL"));
        sDanaRMessages.put(Integer.valueOf(17138), new DanaRMessage("CMD_HISTORY_NEW"));

        sDanaRMessages.put(Integer.valueOf(16881), new DanaRMessage("CMD_HISTORY_ALL_DONE"));
        sDanaRMessages.put(Integer.valueOf(17137), new DanaRMessage("CMD_HISTORY_NEW_DONE"));

        sDanaRMessages.put(Integer.valueOf(1552), new DanaRMessage("CMD_PUMP_ALARM_TIEOUT"));
    }

    public static int byteArrayToInt(byte[] bArr,int offset, int lenght) {
        offset+=6;
        switch (lenght) {
            case 1:
                return bArr[0+offset] & 255;
            case 2:
                return ((bArr[0+offset] & 255) << 8) + (bArr[1+offset] & 255);
            case 3:
                return (((bArr[2+offset] & 255) << 16) + ((bArr[1+offset] & 255) << 8)) + (bArr[0+offset] & 255);
            case 4:
                return ((((bArr[3+offset] & 255) << 24) + ((bArr[2+offset] & 255) << 16)) + ((bArr[1+offset] & 255) << 8)) + (bArr[0+offset] & 255);
            default:
                return -1;
        }
    }
}
