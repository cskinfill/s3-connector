/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.simpleapi;

/**
 * The posible versioning status. Notice: they follow the AmazonS3 enums case,
 * instead of the standard Java case.
 */
public enum VersioningStatus
{
    Off, Enabled, Suspended
}
