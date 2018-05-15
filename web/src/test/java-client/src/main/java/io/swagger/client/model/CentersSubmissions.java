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
import org.threeten.bp.LocalDate;

/**
 * CentersSubmissions
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-05-14T22:43:55.759Z")
public class CentersSubmissions {
  @SerializedName("submission_id")
  private String submissionId = null;

  @SerializedName("submission_date")
  private LocalDate submissionDate = null;

  @SerializedName("tier")
  private Integer tier = null;

  @SerializedName("project")
  private String project = null;

  @SerializedName("submission_description")
  private String submissionDescription = null;

  @SerializedName("story_title")
  private String storyTitle = null;

  @SerializedName("observation_count")
  private Integer observationCount = null;

  public CentersSubmissions submissionId(String submissionId) {
    this.submissionId = submissionId;
    return this;
  }

   /**
   * Get submissionId
   * @return submissionId
  **/
  @ApiModelProperty(required = true, value = "")
  public String getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  public CentersSubmissions submissionDate(LocalDate submissionDate) {
    this.submissionDate = submissionDate;
    return this;
  }

   /**
   * Get submissionDate
   * @return submissionDate
  **/
  @ApiModelProperty(required = true, value = "")
  public LocalDate getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(LocalDate submissionDate) {
    this.submissionDate = submissionDate;
  }

  public CentersSubmissions tier(Integer tier) {
    this.tier = tier;
    return this;
  }

   /**
   * Get tier
   * minimum: 1
   * maximum: 3
   * @return tier
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getTier() {
    return tier;
  }

  public void setTier(Integer tier) {
    this.tier = tier;
  }

  public CentersSubmissions project(String project) {
    this.project = project;
    return this;
  }

   /**
   * Get project
   * @return project
  **/
  @ApiModelProperty(required = true, value = "")
  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public CentersSubmissions submissionDescription(String submissionDescription) {
    this.submissionDescription = submissionDescription;
    return this;
  }

   /**
   * Get submissionDescription
   * @return submissionDescription
  **/
  @ApiModelProperty(required = true, value = "")
  public String getSubmissionDescription() {
    return submissionDescription;
  }

  public void setSubmissionDescription(String submissionDescription) {
    this.submissionDescription = submissionDescription;
  }

  public CentersSubmissions storyTitle(String storyTitle) {
    this.storyTitle = storyTitle;
    return this;
  }

   /**
   * Get storyTitle
   * @return storyTitle
  **/
  @ApiModelProperty(value = "")
  public String getStoryTitle() {
    return storyTitle;
  }

  public void setStoryTitle(String storyTitle) {
    this.storyTitle = storyTitle;
  }

  public CentersSubmissions observationCount(Integer observationCount) {
    this.observationCount = observationCount;
    return this;
  }

   /**
   * Get observationCount
   * @return observationCount
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getObservationCount() {
    return observationCount;
  }

  public void setObservationCount(Integer observationCount) {
    this.observationCount = observationCount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CentersSubmissions centersSubmissions = (CentersSubmissions) o;
    return Objects.equals(this.submissionId, centersSubmissions.submissionId) &&
        Objects.equals(this.submissionDate, centersSubmissions.submissionDate) &&
        Objects.equals(this.tier, centersSubmissions.tier) &&
        Objects.equals(this.project, centersSubmissions.project) &&
        Objects.equals(this.submissionDescription, centersSubmissions.submissionDescription) &&
        Objects.equals(this.storyTitle, centersSubmissions.storyTitle) &&
        Objects.equals(this.observationCount, centersSubmissions.observationCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(submissionId, submissionDate, tier, project, submissionDescription, storyTitle, observationCount);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CentersSubmissions {\n");
    
    sb.append("    submissionId: ").append(toIndentedString(submissionId)).append("\n");
    sb.append("    submissionDate: ").append(toIndentedString(submissionDate)).append("\n");
    sb.append("    tier: ").append(toIndentedString(tier)).append("\n");
    sb.append("    project: ").append(toIndentedString(project)).append("\n");
    sb.append("    submissionDescription: ").append(toIndentedString(submissionDescription)).append("\n");
    sb.append("    storyTitle: ").append(toIndentedString(storyTitle)).append("\n");
    sb.append("    observationCount: ").append(toIndentedString(observationCount)).append("\n");
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

