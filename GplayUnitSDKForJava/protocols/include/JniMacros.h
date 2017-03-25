#ifndef __JNI_MACROS_H__
#define __JNI_MACROS_H__

#define return_if_fails(cond) if (!(cond)) return;
#define return_val_if_fails(cond, ret) if(!(cond)) return (ret);


#endif // __JNI_MACROS_H__
