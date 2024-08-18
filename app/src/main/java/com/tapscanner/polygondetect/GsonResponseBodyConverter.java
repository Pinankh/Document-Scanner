package com.tapscanner.polygondetect;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;
    private final Gson gson;

    GsonResponseBodyConverter(Gson gson2, TypeAdapter<T> adapter2) {
        this.gson = gson2;
        this.adapter = adapter2;
    }

    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = this.gson.newJsonReader(value.charStream());
        try {
            T result = this.adapter.read(jsonReader);
            if (jsonReader.peek() == JsonToken.END_DOCUMENT) {
                return result;
            }
            throw new JsonIOException("JSON document was not fully consumed.");
        } finally {
            value.close();
        }
    }
}
