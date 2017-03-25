#ifndef __UNITSDKUTIL_H__
#define __UNITSDKUTIL_H__

#include "TypeParam.h"
#include "UnitSDK.h"
#include "string.h"
#include <list>
#include <vector>
#include <jni.h>

using namespace std;
namespace gplay { namespace framework {

struct UnitSDKParam
{
    typedef enum{
        kParamTypeNull = 0,
        kParamTypeInt,
        kParamTypeFloat,
        kParamTypeBool,
        kParamTypeString,
        kParamTypeStringMap,
        kParamTypeMap,
    } ParamType;

    UnitSDKParam(int nValue) {
        _intValue = nValue;
        _type = kParamTypeInt;
    }

    UnitSDKParam(float nValue) {
        _floatValue = nValue;
        _type = kParamTypeFloat;
    }

    UnitSDKParam(bool nValue) {
        _boolValue = nValue;
        _type = kParamTypeBool;
    }

    UnitSDKParam(char * nValue) {
        _strValue = nValue;
        _type = kParamTypeString;
    }

    int getIntValue(){ return _intValue;};
    float getFloatValue(){ return _floatValue;};
    bool getBoolValue(){ return _boolValue;};
    char* getStringValue(){ return _strValue;};
    char* getStrMapValue(){ return _strMapValue;};
    int getCurrentType(){return _type;};

private:
    ParamType _type;
    int _intValue;
    float _floatValue;
    bool _boolValue;
    char * _strValue;
    char * _strMapValue;

};
class UnitSDKForJavaUtils
{
public:
    static UnitSDKForJavaUtils * getInstance();

    bool isBoolType( JNIEnv*  env, jobject obj );

    bool isIntType( JNIEnv* env, jobject obj );

    bool isLongType( JNIEnv* env, jobject obj );

    bool isFloatType( JNIEnv* env, jobject obj );

    bool isDoubleType( JNIEnv* env, jobject obj );

    bool isStringType( JNIEnv* env, jobject obj );

    int jInt2Int( JNIEnv* env, jobject obj );

    float jFloat2Float( JNIEnv* env, jobject obj );

    long jLong2Long( JNIEnv* env, jobject obj );

    float jDouble2Float( JNIEnv* env, jobject obj );

    map<string,string> jobject2Map( JNIEnv* env, jobject obj );

    vector<TypeParam> jobject2TypeParam(JNIEnv *env, jobject obj);

    map<string,string> Char2Map(char* charMap);
    list<string> Char2List(char* charList);

    string List2String(list<string>);

    void split(const string& src, const string& separator, vector<string>& dest);

    vector<TypeParam> UnitSDKParam2TypeParam(UnitSDKParam unidSDKParam[],int count);

    string AllGoodsInfo2String(TAllGoodsInfo info);
private:
    UnitSDKForJavaUtils();
    virtual ~UnitSDKForJavaUtils();

    static UnitSDKForJavaUtils * _pInstance;
};

}} // namespace gplay::framework::

#endif
