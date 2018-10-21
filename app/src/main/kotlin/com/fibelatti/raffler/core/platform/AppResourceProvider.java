package com.fibelatti.raffler.core.platform;

import android.content.Context;
import android.util.Log;

import com.fibelatti.raffler.core.provider.ResourceProvider;
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

    @NotNull
    private Context context;
    @NotNull
    private Gson gson;

    @Inject
    public AppResourceProvider(@NotNull Context context, @NotNull Gson gson) {
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
    public <T> T getJsonFromAssets(@NotNull String fileName, @NotNull TypeToken<T> type) {
        try (InputStreamReader reader = new InputStreamReader(context.getAssets().open(fileName))) {
            return gson.fromJson(reader, type.getType());
        } catch (Exception exception) {
            Log.d(TAG, TAG + ".getJsonFromAssets", exception);
        }

        return null;
    }
}
