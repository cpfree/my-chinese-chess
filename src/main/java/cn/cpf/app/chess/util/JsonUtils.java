package cn.cpf.app.chess.util;

import cn.cpf.app.chess.ctrl.Situation;
import cn.cpf.app.chess.ctrl.SituationRecord;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepRecord;
import cn.cpf.app.chess.swing.ChessPiece;
import com.github.cosycode.common.ext.hub.LazySingleton;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <b>Description : </b> Json 解析工具类
 * <p>
 * <b>created in </b> 2021/12/20
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    private static class ChessPieceGsonSerializer implements JsonSerializer<ChessPiece>, JsonDeserializer<ChessPiece> {

        @Override
        public ChessPiece deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final String name = jsonObject.getAsJsonPrimitive("name").getAsString();
            final String piece = jsonObject.getAsJsonPrimitive("piece").getAsString();
            final Place place = context.deserialize(jsonObject.getAsJsonObject("place"), Place.class);
            return new ChessPiece(name, Piece.valueOf(piece), place);
        }

        @Override
        public JsonElement serialize(cn.cpf.app.chess.swing.ChessPiece src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("name", src.name);
            object.addProperty("piece", src.piece.name());
            object.add("place", context.serialize(src.getPlace()));
            return object;
        }

    }

    private static class PlaceSerializer implements JsonSerializer<Place>, JsonDeserializer<Place> {

        @Override
        public JsonElement serialize(Place src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("x", src.x);
            object.addProperty("y", src.y);
            return object;
        }

        @Override
        public Place deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final int x = jsonObject.getAsJsonPrimitive("x").getAsInt();
            final int y = jsonObject.getAsJsonPrimitive("y").getAsInt();
            if (x == -1 || y == -1) {
                return Place.NULL_PLACE;
            } else {
                return Place.of(x, y);
            }
        }
    }

    private static class SituationRecordSerializer implements JsonDeserializer<SituationRecord> {

        @Override
        public SituationRecord deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final List<StepRecord> records = context.deserialize(jsonObject.getAsJsonArray("records"),
                    new TypeToken<List<StepRecord>>() {}.getType());
            return new SituationRecord(records);
        }
    }

    private static class SituationSerializer implements JsonDeserializer<Situation> {

        @Override
        public Situation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final SituationRecord deserialize = context.deserialize(jsonObject.getAsJsonObject("situationRecord"), SituationRecord.class);
            final LocalDateTime situationStartTime = context.deserialize(jsonObject.getAsJsonObject("situationStartTime"), LocalDateTime.class);
            final Part nextPart = Part.valueOf(jsonObject.getAsJsonPrimitive("nextPart").getAsString());
            final List<ChessPiece> pieceList = context.deserialize(jsonObject.getAsJsonArray("pieceList"), new TypeToken<List<ChessPiece>>() {
            }.getType());
            return new Situation(pieceList, deserialize, nextPart, situationStartTime);
        }
    }

    /**
     * GsonBuilder 单例
     */
    private static final LazySingleton<GsonBuilder> gsonBuilderSingleton = LazySingleton.of(() -> {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Place.class, new PlaceSerializer());
        gsonBuilder.registerTypeAdapter(ChessPiece.class, new ChessPieceGsonSerializer());
        gsonBuilder.registerTypeAdapter(Situation.class, new SituationSerializer());
        gsonBuilder.registerTypeAdapter(SituationRecord.class, new SituationRecordSerializer());
        return gsonBuilder;
    });


    public static String toJson(Object object) {
        return gsonBuilderSingleton.instance().create().toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gsonBuilderSingleton.instance().create().fromJson(json, classOfT);
    }

}
