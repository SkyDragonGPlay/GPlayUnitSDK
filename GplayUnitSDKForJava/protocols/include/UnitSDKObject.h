#ifndef __GPLAY_GPLAYUNITSDKIMPL_H__
#define __GPLAY_GPLAYUNITSDKIMPL_H__

#include "UnitSDK.h"
#include <jni.h>
#include <vector>

using namespace std;
namespace gplay { namespace framework {

class UnitSDKObject : public  UnitSDK{
public:
    UnitSDKObject();
    virtual ~UnitSDKObject();
    virtual void init(string appKey,string appSecret,string privateKey);

    virtual void setAsyncActionResultListener(ActionResultListener* listener);
    virtual ActionResultListener* getAsyncActionResultListener();

    virtual string getChannelId();
    virtual bool isFunctionSupported(string functionName);
    virtual void login(string callbackId);
    virtual bool isLogined();
    virtual string getUserID();
    virtual void payForProduct(string callbackId, map<string, string> info);
    virtual string getOrderId();
    virtual void share(string callbackId, map<string, string> info);
    virtual void createShortCut(string callbackId, map<string, string> info);
    
    virtual void callSyncFunc(const char* funcName, TypeParam* param, ...);
    virtual void callSyncFunc(const char* funcName, vector<TypeParam*> params);
    virtual string callSyncStringFunc(const char* funcName, TypeParam* param, ...);
    virtual string callSyncStringFunc(const char* funcName, vector<TypeParam*> params);
    virtual int callSyncIntFunc(const char* funcName, TypeParam* param, ...);
    virtual int callSyncIntFunc(const char* funcName, vector<TypeParam*> params);
    virtual bool callSyncBoolFunc(const char* funcName, TypeParam* param, ...);
    virtual bool callSyncBoolFunc(const char* funcName, vector<TypeParam*> params);
    virtual float callSyncFloatFunc(const char* funcName, TypeParam* param, ...);
    virtual float callSyncFloatFunc(const char* funcName, vector<TypeParam*> params);
    
    virtual void callAsyncFunc(const char* callbackId, const char* funcName, TypeParam * param, ...);
    virtual void callAsyncFunc(const char* callbackId, const char* funcName, vector<TypeParam *> params);
    void onAsyncActionResult(string callbackId, ActionResultCode ret, const char* msg);
    
private:
    static jmethodID getJProxyCallSyncFunctionMethodID();
    static jmethodID getJProxyCallAsynFunctionMethodID();
    static void callJavaSyncFunc(const char* funcName);
    static void callJavaSyncFunc(const char* funcName, vector<TypeParam*> params);
    static bool callJavaSyncBoolFunc(const char* funcName);
    static bool callJavaSyncBoolFunc(const char* funcName, vector<TypeParam*> params);
    static string callJavaSyncStringFunc(const char* funcName);
    static string callJavaSyncStringFunc(const char* funcName, vector<TypeParam*> params);
    static int callJavaSyncIntFunc(const char* funcName);
    static int callJavaSyncIntFunc(const char* funcName, vector<TypeParam*> params);
    static float callJavaSyncFloatFunc(const char* funcName);
    static float callJavaSyncFloatFunc(const char* funcName,vector<TypeParam*> params);
    
    static void callJavaAsyncFunc(const char* callbackId, const char *funcName);
    static void callJavaAsyncFunc(const char* callbackId, const char *funcName, vector<TypeParam*> params);
    
private:
    static jobject jGlobalProxyObj;
    static const string jProxyClass;
    ActionResultListener* pAsyncActionResultListener;
    static map<string, string> _curInfo;
    static map<string, ActionResultListener*> _actionResultListenerMap;
};
    
}} // namespace gplay::framework::


#endif //__GPLAY_GPLAYUNITSDKIMPL_H__