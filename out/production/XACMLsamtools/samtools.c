/* samtools.c */

#include </System/Library/Frameworks/JavaVM.framework/Headers/jni.h>
#include <stdio.h>
#include "samtools/sam_view.c"

JNIEXPORT void JNICALL Java_Main_view
  (JNIEnv * env, jobject jobj)
  {
/*    char  argv[3] = {'view','-bS','/Users/sara/Documents/MIRI/thesis/samtools-1.3.1/examples/toy.sam'};*/
    char *argv[3];
    argv[0] = "view";
    argv[1] = "-bS";
    argv[2] = "/Users/sara/Documents/MIRI/thesis/samtools-1.3.1/examples/toy.sam";

    int argc = 3;
    /*main_samview(3, ["view","-bS","/Users/sara/Documents/MIRI/thesis/samtools-1.3.1/examples/toy.sam"]);*/
    main_samview(argc, argv);
  }