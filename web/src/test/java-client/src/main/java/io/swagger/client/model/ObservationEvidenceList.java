/*
 * CTD2 Dashboard API
 * API access to CTD2 Dashboard
 *
 * OpenAPI spec version: 1.0.0
 * Contact: ocg@mail.nih.gov
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * ObservationEvidenceList
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-05-14T22:43:55.759Z")
public class ObservationEvidenceList {
  /**
   * Gets or Sets propertyClass
   */
  @JsonAdapter(PropertyClassEnum.Adapter.class)
  public enum PropertyClassEnum {
    LABEL("label"),
    
    NUMERIC("numeric"),
    
    URL("url"),
    
    FILE("file");

    private String value;

    PropertyClassEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static PropertyClassEnum fromValue(String text) {
      for (PropertyClassEnum b : PropertyClassEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<PropertyClassEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final PropertyClassEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public PropertyClassEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return PropertyClassEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("class")
  private PropertyClassEnum propertyClass = null;

  @SerializedName("type")
  private String type = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("value")
  private String value = null;

  @SerializedName("units")
  private String units = null;

  @SerializedName("mime_type")
  private String mimeType = null;

  public ObservationEvidenceList propertyClass(PropertyClassEnum propertyClass) {
    this.propertyClass = propertyClass;
    return this;
  }

   /**
   * Get propertyClass
   * @return propertyClass
  **/
  @ApiModelProperty(required = true, value = "")
  public PropertyClassEnum getPropertyClass() {
    return propertyClass;
  }

  public void setPropertyClass(PropertyClassEnum propertyClass) {
    this.propertyClass = propertyClass;
  }

  public ObservationEvidenceList type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(required = true, value = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ObservationEvidenceList description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(required = true, value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ObservationEvidenceList value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @ApiModelProperty(required = true, value = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ObservationEvidenceList units(String units) {
    this.units = units;
    return this;
  }

   /**
   * Get units
   * @return units
  **/
  @ApiModelProperty(value = "")
  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public ObservationEvidenceList mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

   /**
   * Get mimeType
   * @return mimeType
  **/
  @ApiModelProperty(value = "")
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObservationEvidenceList observationEvidenceList = (ObservationEvidenceList) o;
    return Objects.equals(this.propertyClass, observationEvidenceList.propertyClass) &&
        Objects.equals(this.type, observationEvidenceList.type) &&
        Objects.equals(this.description, observationEvidenceList.description) &&
        Objects.equals(this.value, observationEvidenceList.value) &&
        Objects.equals(this.units, observationEvidenceList.units) &&
        Objects.equals(this.mimeType, observationEvidenceList.mimeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyClass, type, description, value, units, mimeType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObservationEvidenceList {\n");
    
    sb.append("    propertyClass: ").append(toIndentedString(propertyClass)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

