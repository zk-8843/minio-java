/*
 * Minio Java Library for Amazon S3 Compatible Cloud Storage, (C) 2015 Minio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.minio;

import io.minio.errors.*;

import com.google.common.io.BaseEncoding;
import com.google.common.base.Strings;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class PostPolicy {
  private static final String ALGORITHM = "AWS4-HMAC-SHA256";

  private String bucketName;
  private String objectName;
  private boolean startsWith;
  private DateTime expirationDate;
  private String contentType;


  public PostPolicy(String bucketName, String objectName, DateTime expirationDate)
    throws InvalidArgumentException {
    this(bucketName, objectName, false, expirationDate);
  }


  /**
   * constructor.
   */
  public PostPolicy(String bucketName, String objectName, boolean startsWith, DateTime expirationDate)
    throws InvalidArgumentException {
    if (bucketName == null) {
      throw new InvalidArgumentException("null bucket name");
    }
    this.bucketName = bucketName;

    if (objectName == null) {
      throw new InvalidArgumentException("null object name or prefix");
    }
    this.objectName = objectName;

    this.startsWith = startsWith;

    if (expirationDate == null) {
      throw new InvalidArgumentException("null expiration date");
    }
    this.expirationDate = expirationDate;
  }


  /**
   * set content type.
   */
  public void setContentType(String contentType) throws InvalidArgumentException {
    if (Strings.isNullOrEmpty(contentType)) {
      throw new InvalidArgumentException("empty content type");
    }

    this.contentType = contentType;
  }


  public String bucketName() {
    return this.bucketName;
  }


  private byte[] marshalJson(ArrayList<String[]> conditions) throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();
    Joiner joiner = Joiner.on("\",\"");

    sb.append("{");

    if (expirationDate != null) {
      sb.append("\"expiration\":" + "\"" + expirationDate.toString(DateFormat.EXPIRATION_DATE_FORMAT) + "\"");
    }

    if (conditions.size() > 0) {
      sb.append(",\"conditions\":[");

      ListIterator<String[]> iterator = conditions.listIterator();
      while (iterator.hasNext()) {
        sb.append("[\"" + joiner.join(iterator.next()) + "\"]");
        if (iterator.hasNext()) {
          sb.append(",");
        }
      }

      sb.append("]");
    }

    sb.append("}");

    return sb.toString().getBytes("UTF-8");
  }


  /**
   * returns form data of this post policy.
   */
  public Map<String,String> formData(String accessKey, String secretKey)
    throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    ArrayList<String[]> conditions = new ArrayList<String[]>();
    Map<String, String> formData = new HashMap<String, String>();

    conditions.add(new String[]{"eq", "$bucket", this.bucketName});
    formData.put("bucket", this.bucketName);

    if (this.startsWith) {
      conditions.add(new String[]{"starts-with", "$key", this.objectName});
      formData.put("key", this.objectName);
    } else {
      conditions.add(new String[]{"eq", "$key", this.objectName});
      formData.put("key", this.objectName);
    }

    if (this.contentType != null) {
      conditions.add(new String[]{"eq", "$Content-Type", this.contentType});
      formData.put("Content-Type", this.contentType);
    }

    conditions.add(new String[]{"eq", "$x-amz-algorithm", ALGORITHM});
    formData.put("x-amz-algorithm", ALGORITHM);

    RequestSigner signer = new RequestSigner(accessKey, secretKey, Regions.INSTANCE.region(this.bucketName));

    String credential = accessKey + "/" + signer.getScope();
    conditions.add(new String[]{"eq", "$x-amz-credential", credential});
    formData.put("x-amz-credential", credential);

    String amzDate = signer.date().toString(DateFormat.AMZ_DATE_FORMAT);
    conditions.add(new String[]{"eq","$x-amz-date", amzDate});
    formData.put("x-amz-date", amzDate);

    String policybase64 = BaseEncoding.base64().encode(this.marshalJson(conditions));
    String signature = signer.postPreSignV4(policybase64);

    formData.put("policy", policybase64);
    formData.put("x-amz-signature", signature);

    return formData;
  }
}