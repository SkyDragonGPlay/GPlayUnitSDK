package com.skydragon.gplay.unitsdk.framework.java;

import java.util.Map;

/**
 * 调用通用扩展接口时, 由于不确定参数类型, 所有参数要统一构造成 UnitSDKParam 对象作为参数
 */
public class UnitSDKParam {

    private ParamType _type;
    private int _intValue;
    private float _floatValue;
    private boolean _boolValue;
    private String _strValue;
    private Map<String, String> _mapValue;

    enum ParamType {
        kParamTypeNull(0),
        kParamTypeInt(1),
        kParamTypeFloat(2),
        kParamTypeBool(3),
        kParamTypeString(4),
        kParamTypeStringMap(5),
        kParamTypeMap(6);

        private int paramType;

        ParamType( int code ) {
            paramType = code;
        }

        public int getParamType() {
            return paramType;
        }
        
    }

    public UnitSDKParam() {
        _type = ParamType.kParamTypeNull;
    }

    public UnitSDKParam(int nValue) {
        _intValue = nValue;
        _type = ParamType.kParamTypeInt;
    }

    public UnitSDKParam(float nValue) {
        _floatValue = nValue;
        _type = ParamType.kParamTypeFloat;
    }

    public UnitSDKParam(boolean nValue) {
        _boolValue = nValue;
        _type = ParamType.kParamTypeBool;
    }

    public UnitSDKParam(String nValue) {
        _strValue = nValue;
        _type = ParamType.kParamTypeString;
    }

    public UnitSDKParam(Map<String, String> nValue) {
        _mapValue = nValue;
        _type = ParamType.kParamTypeMap;
    }


    public int getCurrentType() {
        return _type.paramType;
    }

    public int getIntValue() {
        return _intValue;
    }

    public float getFloatValue() {
        return _floatValue;
    }

    public boolean getBoolValue() {
        return _boolValue;
    }

    public String getStringValue() {
        return _strValue;
    }

    public Map<String, String> getMapValue() {
        return _mapValue;
    }
}
