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

    virtual bool isFunctionSupported(string functionName);
    virtual bool isLogined();

    virtual string getChannelId();
    virtual string getOrderId();
    virtual string getUserID();

    virtual void login(string callbackId);
    virtual void payForProduct(string callbackId, string info);
    virtual void share(string callbackId, string info);
    virtual void createShortCut(string callbackId);
    
    virtual string callSyncStringFunc(string funcName, string jsonParams);
    virtual void callAsyncFunc(string callbackId, string funcName, string jsonParams);

    void onAsyncActionResult(string callbackId, ActionResultCode ret, const char* msg);
    
private:
    static jmethodID getJProxyCallSyncFunctionMethodID();
    static jmethodID getJProxyCallAsynFunctionMethodID();
    static string callJavaSyncStringFunc(string funcName, string jsonParams);
    static void callJavaAsyncFunc(string callbackId, string funcName, string jsonParams);
    
private:
    static jobject jGlobalProxyObj;
    static const string jProxyClass;
    ActionResultListener* pAsyncActionResultListener;
    static map<string, string> _curInfo;
    static map<string, ActionResultListener*> _actionResultListenerMap;
    
public:
    static map<string, string> _payingCallbacks;
};
    
}} // namespace gplay::framework::


#endif //__GPLAY_GPLAYUNITSDKIMPL_H__