package ch.epfl.favo.common;

import java.util.Map;

public interface Document {
  String getId();
  Map<String,Object> toMap();
}
