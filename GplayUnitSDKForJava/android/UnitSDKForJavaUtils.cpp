#include "UnitSDKForJavaUtils.h"
#include "JniHelper.h"
#include <android/log.h>
#include "Utils.h"

#include <stdlib.h>
#include <iostream>
#include <vector>
#include <string>


#define  LOG_TAG    "UnitSDKForJavaUtils"



namespace gplay { namespace framework  {
UnitSDKForJavaUtils* UnitSDKForJavaUtils::_pInstance = NULL;

UnitSDKForJavaUtils::UnitSDKForJavaUtils()
{

}

UnitSDKForJavaUtils::~UnitSDKForJavaUtils()
{

}

UnitSDKForJavaUtils* UnitSDKForJavaUtils::getInstance()
{
    if (_pInstance == NULL) {
        _pInstance = new UnitSDKForJavaUtils();
    }
    return _pInstance;
}

bool UnitSDKForJavaUtils::isBoolType( JNIEnv*  env, jobject obj ) {
    jclass boolClass = env->FindClass("java/lang/Boolean");
    bool b =  env->IsInstanceOf(obj, boolClass);
    env->DeleteLocalRef( boolClass );
    return b;
}

bool UnitSDKForJavaUtils::isIntType( JNIEnv* env, jobject obj ) {
    jclass intClass = env->FindClass("java/lang/Integer");
    bool b =  env->IsInstanceOf(obj, intClass);
    env->DeleteLocalRef( intClass );
    return b;
}

bool UnitSDKForJavaUtils::isLongType( JNIEnv* env, jobject obj ) {
    jclass longClass = env->FindClass("java/lang/Long");
    bool b =  env->IsInstanceOf(obj, longClass);
    env->DeleteLocalRef( longClass );
    return b;
}

bool UnitSDKForJavaUtils::isFloatType( JNIEnv* env, jobject obj ) {
    jclass floatClass = env->FindClass("java/lang/Float");
    bool b =  env->IsInstanceOf(obj, floatClass);
    env->DeleteLocalRef( floatClass );
    return b;
}

bool UnitSDKForJavaUtils::isDoubleType( JNIEnv* env, jobject obj ) {
    jclass doubleClass = env->FindClass("java/lang/Double");
    bool b =  env->IsInstanceOf(obj, doubleClass);
    env->DeleteLocalRef( doubleClass );
    return b;
}

bool UnitSDKForJavaUtils::isStringType( JNIEnv* env, jobject obj ) {
    jclass stringClass = env->FindClass("java/lang/String");
    bool b =  env->IsInstanceOf(obj, stringClass);
    env->DeleteLocalRef( stringClass );
    return b;
}

int UnitSDKForJavaUtils::jInt2Int( JNIEnv* env, jobject obj ) {
    jclass intClass = env->FindClass("java/lang/Integer");
    jmethodID methodID = env->GetMethodID(intClass,"intValue","()I");
    int value = env->CallIntMethod( obj, methodID );
    env->DeleteLocalRef( intClass );
    return value;
}

float UnitSDKForJavaUtils::jFloat2Float( JNIEnv* env, jobject obj ) {
    jclass floatClass = env->FindClass("java/lang/Float");
    jmethodID methodID = env->GetMethodID(floatClass,"floatValue","()F");
    float f =  env->CallFloatMethod( obj, methodID );
    env->DeleteLocalRef( floatClass );
    return f;
}

long UnitSDKForJavaUtils::jLong2Long( JNIEnv* env, jobject obj ) {
    jclass longClass = env->FindClass("java/lang/Long");
    jmethodID methodID = env->GetMethodID(longClass,"longValue","()J");
    long l =  env->CallFloatMethod( obj, methodID );
    env->DeleteLocalRef( longClass );
    return l;
}

float UnitSDKForJavaUtils::jDouble2Float( JNIEnv* env, jobject obj ) {
    jclass doubleClass = env->FindClass("java/lang/Double");
    jmethodID methodID = env->GetMethodID(doubleClass,"doubleValue","()D");
    double d =  env->CallFloatMethod( obj, methodID );
    env->DeleteLocalRef( doubleClass );
    return (float)d;
}

map<string,string> UnitSDKForJavaUtils::jobject2Map( JNIEnv* env, jobject obj )
{
    jclass clazz = env->GetObjectClass(obj);
    jmethodID getMethodID = env->GetMethodID( clazz, "get", "(Ljava/lang/Object;)Ljava/lang/Object;" );
    jmethodID keySetMethodID = env->GetMethodID(clazz, "keySet", "()Ljava/util/Set;");

    jobject setObj = env->CallObjectMethod( obj, keySetMethodID );
    jclass setClass = env->GetObjectClass( setObj );
    jmethodID toArrayMethodID = env->GetMethodID(setClass, "toArray", "()[Ljava/lang/Object;");

    jobjectArray objs = (jobjectArray)env->CallObjectMethod( setObj, toArrayMethodID ) ;
    int length = env->GetArrayLength( objs );

    map<string, string> map;
    for( int i = 0; i < length; i++ ) {
        jobject key = env->GetObjectArrayElement( objs, i );
        jobject value = env->CallObjectMethod( obj, getMethodID, key );
        string sKey = JniHelper::jstring2string((jstring)key);
        string sValue = JniHelper::jstring2string((jstring)value);
        map.insert ( pair<string,string>(sKey, sValue) );
    }
    env->DeleteLocalRef(objs);
    env->DeleteLocalRef(setClass);
    env->DeleteLocalRef(setObj);
    env->DeleteLocalRef(clazz);
    return map;
}

vector<TypeParam> UnitSDKForJavaUtils::jobject2TypeParam( JNIEnv* env, jobject param )
{
    vector<TypeParam> vectorTypeParam;
    jclass clazz = env->GetObjectClass(param);
    jmethodID getMethodID = env->GetMethodID(clazz, "size", "()I");
    int size = (int) env->CallIntMethod(param, getMethodID);
    for(int i = 0; i< size ;i++)
    {
        getMethodID = env->GetMethodID(clazz, "get", "(I)Ljava/lang/Object;");
        jobject unitSDKParam = env->CallObjectMethod(param, getMethodID,(jint)i);
        clazz = env->FindClass("com/skydragon/gplay/unitsdk/framework/java/UnitSDKParam");
        getMethodID = env->GetMethodID(clazz, "getCurrentType", "()I");
        int type = (int) env->CallIntMethod(unitSDKParam, getMethodID);
        TypeParam params;
        switch (type) {
            case TypeParam::kParamTypeInt: {
                getMethodID = env->GetMethodID(clazz, "getIntValue", "()I");
                int value = (int) env->CallIntMethod(unitSDKParam, getMethodID);
                TypeParam par(value);
                params = par;
            }
                break;
            case TypeParam::kParamTypeFloat: {
                getMethodID = env->GetMethodID(clazz, "getFloatValue", "()F");
                float value = (float) env->CallFloatMethod(unitSDKParam, getMethodID);
                TypeParam par(value);
                params = par;
            }
                break;
            case TypeParam::kParamTypeBool: {
                getMethodID = env->GetMethodID(clazz, "getBoolValue", "()Z");
                bool value = (bool) env->CallBooleanMethod(unitSDKParam, getMethodID);
                TypeParam par(value);
                params = par;
            }
                break;
            case TypeParam::kParamTypeString: {
                getMethodID = env->GetMethodID(clazz, "getStringValue",
                                               "()Ljava/lang/String;");
                jstring jvalue = (jstring) env->CallObjectMethod(unitSDKParam, getMethodID);
                TypeParam par(JniHelper::jstring2string(jvalue).c_str());
                params = par;
            }
                break;
            case TypeParam::kParamTypeMap: {
                std::map < std::string, std::string > typeParams;
                jmethodID getStrMapMethodID = env->GetMethodID(clazz, "getMapValue",
                                                               "()Ljava/util/Map;");
                jobject map = env->CallObjectMethod(unitSDKParam, getStrMapMethodID);

                jclass clazz = env->FindClass("java/util/Map");
                jmethodID getSetMethodID = env->GetMethodID(clazz, "entrySet",
                                                            "()Ljava/util/Set;");
                jobject set = env->CallObjectMethod(map, getSetMethodID);

                clazz = env->FindClass("java/util/Set");
                jmethodID getIteratorMethodID = env->GetMethodID(clazz, "iterator",
                                                                 "()Ljava/util/Iterator;");
                jobject iterator = env->CallObjectMethod(set, getIteratorMethodID);

                clazz = env->FindClass("java/util/Iterator");
                jmethodID hasNext = env->GetMethodID(clazz, "hasNext", "()Z");
                jmethodID next = env->GetMethodID(clazz, "next",
                                                  "()Ljava/lang/Object;");

                clazz = env->FindClass("java/util/Map$Entry");
                jmethodID getKeyMethodID = env->GetMethodID(clazz, "getKey",
                                                            "()Ljava/lang/Object;");
                jmethodID getValueMethodID = env->GetMethodID(clazz, "getValue",
                                                              "()Ljava/lang/Object;");
                while (env->CallBooleanMethod(iterator, hasNext)) {
                    jobject entry = env->CallObjectMethod(iterator, next);
                    jstring jkey = (jstring) env->CallObjectMethod(entry,
                                                                   getKeyMethodID);
                    jstring jvalue = (jstring) env->CallObjectMethod(entry,
                                                                     getValueMethodID);
                    typeParams[JniHelper::jstring2string(jkey)] =
                            JniHelper::jstring2string(jvalue);
                    env->DeleteLocalRef(entry);
                    env->DeleteLocalRef(jkey);
                    env->DeleteLocalRef(jvalue);

                }
                env->DeleteLocalRef(clazz);
                env->DeleteLocalRef(map);
                env->DeleteLocalRef(set);
                env->DeleteLocalRef(iterator);

                TypeParam par(typeParams);
                params = par;
            }
                break;
        }
        env->DeleteLocalRef(unitSDKParam);
        vectorTypeParam.push_back(params);
    }
    env->DeleteLocalRef(clazz);
    return vectorTypeParam;
}

void UnitSDKForJavaUtils::split(const string& src, const string& separator, vector<string>& dest)
{
    string str = src;
    string substring;
    string::size_type start = 0, index;

    do
    {
        index = str.find_first_of(separator,start);
        if (index != string::npos)
        {
            substring = str.substr(start,index-start);
            dest.push_back(substring);
            start = str.find_first_not_of(separator,index);
            if (start == string::npos) return;
        }
    }while(index != string::npos);

    //the last token
    substring = str.substr(start);
    dest.push_back(substring);
}

map<string,string> UnitSDKForJavaUtils:: Char2Map(char* charMap)
{
    map<string,string> map;
    if( NULL == charMap )
    {
        return map;
    }
    vector<string> tokens;
    split(charMap,"&",tokens);
    for (size_t i = 0; i < tokens.size(); i++)
    {
        vector<string> temp;
        split(tokens[i],"=",temp);
        map[temp[0]]=temp[1];
    }
    return map;

}
list<string> UnitSDKForJavaUtils:: Char2List(char* charList)
{
    list<string> list;
    if( NULL == charList )
    {
        return list;
    }
    vector<string> tokens;
    split(charList,"&",tokens);
    for (size_t i = 0; i < tokens.size(); i++)
    {
        list.push_back(tokens[i]);
    }
    return list;
}

vector<TypeParam> UnitSDKForJavaUtils::UnitSDKParam2TypeParam(UnitSDKParam unidSDKParam[],int count)
{
    vector<TypeParam> params;
    for(int i= 0;i< count;i++)
    {

        TypeParam param;
        switch(unidSDKParam[i].getCurrentType())
        {
            case TypeParam::kParamTypeInt:
            {
                int value =unidSDKParam[i].getIntValue();
                TypeParam par(value);
                param = par;
            }
                break;
            case TypeParam::kParamTypeFloat:
            {
                float value = unidSDKParam[i].getFloatValue();
                TypeParam par(value);
                param = par;
            }
                break;
            case TypeParam::kParamTypeBool:
            {
                bool value = unidSDKParam[i].getBoolValue();
                TypeParam par(value);
                param = par;
            }
                break;
            case TypeParam::kParamTypeString:
            {
                string value = unidSDKParam[i].getStringValue();
                TypeParam par(value.c_str());
                param = par;
            }
                break;
            case TypeParam::kParamTypeStringMap:
            {
                map<string,string> value = Char2Map(unidSDKParam[i].getStrMapValue());
                TypeParam par(value);
                param = par;
            }
                break;
        }
        params.push_back(param);
    }
    return params;
}
string UnitSDKForJavaUtils::List2String(list<string> value)
{
    string temp = "";
    list<string>::iterator itor;
    itor=value.begin();
    while(itor!=value.end())
    {
        if(temp == "")
        {
            temp.append(*itor);
        }
        else
        {
            temp.append("&");
            temp.append(*itor);
        }
        itor++;
    }
    return temp;

}

string UnitSDKForJavaUtils::AllGoodsInfo2String(TAllGoodsInfo dic)
{
    string value = "{";
    map<string, TGoodsInfo >::iterator iterParent;
    iterParent = dic.begin();
    while(iterParent != dic.end())
    {
        value.append(iterParent->first);
        value.append("={");
        map<string, string> infoChild = iterParent->second;
        map<string, string >::iterator iterChild;
        iterChild = infoChild.begin();
        while(iterChild != infoChild.end())
        {
            value.append(iterChild->first);
            value.append("=");
            value.append(iterChild->second);
            iterChild++;
            if(iterChild != infoChild.end())
                value.append(", ");
        }
        iterParent++;
        if(iterParent != dic.end())
            value.append("}, ");
    }
    value.append("}");
    return value;
}
}}




