package ch.epfl.favo.database;

import java.util.Map;

public interface Document {
  String getId();

  Map<String, Object> toMap();
}
