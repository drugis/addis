package org.drugis.addis;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Immutable Representation of a dot-separated version.
 * 
 * This representation
 * allows individual sections of the version to be wild-carded and allows
 * for comparisons between Versions with different numbers of version
 * subsections to be compared.  When comparing Versions, each version
 * subsection is compared from left to right.  If one Version doesn't have
 * a version subsection at the current index, the value of versionPadding
 * is used for this comparison.  Version subsections with the wild-card value "*"
 * care considered equal.  The value returned by compareTo() is the value of the
 * first non-equal version subsection or zero if all subsections match.
 * 
 * Due to the support for wild-cards, this class has a natural ordering
 * that is inconsistent with equals.  For example,
 * <code>Version("5", "*").compareTo(Version("5.0", "*") == 0</code>
 * <code>Version("5", "*").equals(Version("5.0", "*") == false;</code>
 * @author Blake Sullivan
 */
public final class Version implements Comparable<Version>
{
  /**
   * Creates a Version instance from the dot-separated Version String using null as the padding
   * @param version The dot-separated version to represent
   * @throws NullPointerException if the version is null
   * @throws IllegalArgumentException if the version is an empty String
   * @see #Version(String, String)
   */
  public Version(String version)
  {
    this(version, null);
  }
  
  /**
   * Creates a Version instance from the dot-separated Version String and the
   * versionPadding.
   * @param version The dot-separated version to represent
   * @param versionPadding The value to return for sub-version sections
   * requested beyond the sub-version sections present in the version String
   * @throws NullPointerException if version or versionPadding are null
   * @throws IllegalArgumentException if version or versionPadding are the
   * empty String
   */
  public Version(String version, String versionPadding)
  {
    _checkNonEmptyString(version, "version");
    if (versionPadding == null)
    {
      versionPadding = "";
    }
    
    // build the array of subversions
    _versions = _DOT_SPLITTER.split(version, 0);
    _versionPadding = versionPadding;
    
    // since we're immutable, we might as well calculate this up front
    // while we still have the String version around
    _hashCode = version.hashCode() * 37 + versionPadding.hashCode();
  }

  /**
   * When comparing Versions, each version
   * subsection is compared from left to right.  If one Version doesn't have
   * a version subsection at the current index, the value of versionPadding
   * is used for this comparison.  Version subsections with the wild-card value "*"
   * care considered equal.  The value returned by compareTo() is the value of the
   * first non-equal version subsection or zero if all subsections match.
   * @param otherVersion The Version object to compare this Version Object with
   * @return a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  public int compareTo(Version otherVersion)
  {
    int ourVersionCount = _versions.length;
    int otherVersionCount = otherVersion._versions.length;
    
    int compareCount = (ourVersionCount > otherVersionCount)
                         ? ourVersionCount
                         : otherVersionCount;
    
    for (int versionIndex = 0; versionIndex < compareCount; versionIndex++)
    {
      String ourSubVersion = _getSubVersion(versionIndex);
      String otherSubVersion = otherVersion._getSubVersion(versionIndex);
      
      // treat "*" wildcard as equals
      if ("*".equals(ourSubVersion) || "*".equals(otherSubVersion))
      {
        continue;
      }
      else
      {
        // compare the sub-result
        int result = ourSubVersion.compareTo(otherSubVersion);
        
        // not equal, so return the result
        if (result != 0)
          return result;
      }
    }
    
    // equivalent
    return 0;
  }
  
  @Override
  public String toString()
  {
    // rebuild the initial version string from the split array
    StringBuilder versionBuilder = new StringBuilder();
    int versionCount = _versions.length;
    
    for (int i = 0;;)
    {
      versionBuilder.append(_versions[i]);
      
      i++;
      
      if (i != versionCount)
        versionBuilder.append('.');
      else
        break;
    }
        
    return versionBuilder.toString();
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    else if (!(o instanceof Version))
      return false;
    else
    {
      Version otherVersion = (Version)o;
      
      // we are equal if all of version content and padding are equal
      return _versionPadding.equals(otherVersion._versionPadding) &&
             Arrays.equals(_versions, otherVersion._versions);
    }
  }
  
  @Override
  public int hashCode()
  {
    // used cached version
    return _hashCode;
  }
  
  /**
   * Returns the contents of the sub-version section of the overall version,
   * padding the result with the version padding if the version section
   * index is greater than the number of actual version sections in this
   * version
   * @param versionIndex index of the "." version section from the left side
   * of the version string.
   * @return The content of the version section if available, otehrwise the
   * versionPadding
   */
  private String _getSubVersion(int versionIndex)
  {
    if (versionIndex >= _versions.length)
      return _versionPadding;
    else
      return _versions[versionIndex];
  }

  
  private void _checkNonEmptyString(String checkedString, String identifier)
  {
    if (checkedString == null)
      throw new NullPointerException(identifier + " must be non-null");
    
    if (checkedString.length() == 0)
      throw new IllegalArgumentException(identifier + " must be non-empty");
  }
  
  private final String[] _versions;
  private final String _versionPadding;
  private final int _hashCode;
  
  // cache the compiled splitter
  private static final Pattern _DOT_SPLITTER = Pattern.compile("\\.");
}
