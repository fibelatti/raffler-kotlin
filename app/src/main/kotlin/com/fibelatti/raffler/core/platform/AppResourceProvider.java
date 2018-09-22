package com.fibelatti.raffler.core.platform;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fibelatti.raffler.core.providers.ResourceProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;

import javax.inject.Inject;

/***
 * This class is implemented in Java because Kotlin is not properly translating
 * `varargs formatArgs: Any` when invoking `getString(resId, formatArgs)`
 */
public class AppResourceProvider implements ResourceProvider {
    private static final String TAG = AppResourceProvider.class.getSimpleName();

    @NonNull
    private Context context;
    @NonNull
    private Gson gson;

    @Inject
    public AppResourceProvider(@NonNull Context context, @NonNull Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    @NotNull
    @Override
    public String getString(int resId) {
        return context.getString(resId);
    }

    @NotNull
    @Override
    public String getString(int resId, @NotNull Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    @Nullable
    @Override
    public <T> T getJsonFromAssets(@NonNull String fileName, @NonNull TypeToken<T> type) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open(fileName));
            return gson.fromJson(reader, type.getType());
        } catch (Exception exception) {
            Log.d(TAG, TAG + ".getJsonFromAssets", exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {

                }
            }
        }

        return null;
    }
}
