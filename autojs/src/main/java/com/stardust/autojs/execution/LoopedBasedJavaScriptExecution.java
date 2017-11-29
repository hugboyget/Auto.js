package com.stardust.autojs.execution;

import android.os.MessageQueue;
import android.util.Log;

import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.runtime.api.Loopers;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/10/27.
 */

public class LoopedBasedJavaScriptExecution extends RunnableScriptExecution {

    public LoopedBasedJavaScriptExecution(ScriptEngineManager manager, ScriptExecutionTask task) {
        super(manager, task);
    }


    protected Object doExecution(final ScriptEngine engine) {
        engine.setTag(ScriptEngine.TAG_SOURCE, getSource());
        getListener().onStart(this);
        long delay = getConfig().delay;
        sleep(delay);
        final LoopBasedJavaScriptEngine javaScriptEngine = (LoopBasedJavaScriptEngine) engine;
        final long interval = getConfig().interval;
        javaScriptEngine.getRuntime().loopers.setLooperQuitHandler(new Loopers.LooperQuitHandler() {
            long times = getConfig().loopTimes == 0 ? Integer.MAX_VALUE : getConfig().loopTimes;

            @Override
            public boolean shouldQuit() {
                times--;
                if (times > 0) {
                    sleep(interval);
                    javaScriptEngine.execute(getSource());
                    return false;
                }
                javaScriptEngine.getRuntime().loopers.setLooperQuitHandler(null);
                return true;
            }
        });
        javaScriptEngine.execute(getSource());
        return null;
    }

    @Override
    public JavaScriptSource getSource() {
        return (JavaScriptSource) super.getSource();
    }

}
