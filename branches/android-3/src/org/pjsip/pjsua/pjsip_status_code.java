/**
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pjsip.pjsua;

public enum pjsip_status_code {
  PJSIP_SC_TRYING(pjsuaJNI.PJSIP_SC_TRYING_get()),
  PJSIP_SC_RINGING(pjsuaJNI.PJSIP_SC_RINGING_get()),
  PJSIP_SC_CALL_BEING_FORWARDED(pjsuaJNI.PJSIP_SC_CALL_BEING_FORWARDED_get()),
  PJSIP_SC_QUEUED(pjsuaJNI.PJSIP_SC_QUEUED_get()),
  PJSIP_SC_PROGRESS(pjsuaJNI.PJSIP_SC_PROGRESS_get()),
  PJSIP_SC_OK(pjsuaJNI.PJSIP_SC_OK_get()),
  PJSIP_SC_ACCEPTED(pjsuaJNI.PJSIP_SC_ACCEPTED_get()),
  PJSIP_SC_MULTIPLE_CHOICES(pjsuaJNI.PJSIP_SC_MULTIPLE_CHOICES_get()),
  PJSIP_SC_MOVED_PERMANENTLY(pjsuaJNI.PJSIP_SC_MOVED_PERMANENTLY_get()),
  PJSIP_SC_MOVED_TEMPORARILY(pjsuaJNI.PJSIP_SC_MOVED_TEMPORARILY_get()),
  PJSIP_SC_USE_PROXY(pjsuaJNI.PJSIP_SC_USE_PROXY_get()),
  PJSIP_SC_ALTERNATIVE_SERVICE(pjsuaJNI.PJSIP_SC_ALTERNATIVE_SERVICE_get()),
  PJSIP_SC_BAD_REQUEST(pjsuaJNI.PJSIP_SC_BAD_REQUEST_get()),
  PJSIP_SC_UNAUTHORIZED(pjsuaJNI.PJSIP_SC_UNAUTHORIZED_get()),
  PJSIP_SC_PAYMENT_REQUIRED(pjsuaJNI.PJSIP_SC_PAYMENT_REQUIRED_get()),
  PJSIP_SC_FORBIDDEN(pjsuaJNI.PJSIP_SC_FORBIDDEN_get()),
  PJSIP_SC_NOT_FOUND(pjsuaJNI.PJSIP_SC_NOT_FOUND_get()),
  PJSIP_SC_METHOD_NOT_ALLOWED(pjsuaJNI.PJSIP_SC_METHOD_NOT_ALLOWED_get()),
  PJSIP_SC_NOT_ACCEPTABLE(pjsuaJNI.PJSIP_SC_NOT_ACCEPTABLE_get()),
  PJSIP_SC_PROXY_AUTHENTICATION_REQUIRED(pjsuaJNI.PJSIP_SC_PROXY_AUTHENTICATION_REQUIRED_get()),
  PJSIP_SC_REQUEST_TIMEOUT(pjsuaJNI.PJSIP_SC_REQUEST_TIMEOUT_get()),
  PJSIP_SC_GONE(pjsuaJNI.PJSIP_SC_GONE_get()),
  PJSIP_SC_REQUEST_ENTITY_TOO_LARGE(pjsuaJNI.PJSIP_SC_REQUEST_ENTITY_TOO_LARGE_get()),
  PJSIP_SC_REQUEST_URI_TOO_LONG(pjsuaJNI.PJSIP_SC_REQUEST_URI_TOO_LONG_get()),
  PJSIP_SC_UNSUPPORTED_MEDIA_TYPE(pjsuaJNI.PJSIP_SC_UNSUPPORTED_MEDIA_TYPE_get()),
  PJSIP_SC_UNSUPPORTED_URI_SCHEME(pjsuaJNI.PJSIP_SC_UNSUPPORTED_URI_SCHEME_get()),
  PJSIP_SC_BAD_EXTENSION(pjsuaJNI.PJSIP_SC_BAD_EXTENSION_get()),
  PJSIP_SC_EXTENSION_REQUIRED(pjsuaJNI.PJSIP_SC_EXTENSION_REQUIRED_get()),
  PJSIP_SC_SESSION_TIMER_TOO_SMALL(pjsuaJNI.PJSIP_SC_SESSION_TIMER_TOO_SMALL_get()),
  PJSIP_SC_INTERVAL_TOO_BRIEF(pjsuaJNI.PJSIP_SC_INTERVAL_TOO_BRIEF_get()),
  PJSIP_SC_TEMPORARILY_UNAVAILABLE(pjsuaJNI.PJSIP_SC_TEMPORARILY_UNAVAILABLE_get()),
  PJSIP_SC_CALL_TSX_DOES_NOT_EXIST(pjsuaJNI.PJSIP_SC_CALL_TSX_DOES_NOT_EXIST_get()),
  PJSIP_SC_LOOP_DETECTED(pjsuaJNI.PJSIP_SC_LOOP_DETECTED_get()),
  PJSIP_SC_TOO_MANY_HOPS(pjsuaJNI.PJSIP_SC_TOO_MANY_HOPS_get()),
  PJSIP_SC_ADDRESS_INCOMPLETE(pjsuaJNI.PJSIP_SC_ADDRESS_INCOMPLETE_get()),
  PJSIP_AC_AMBIGUOUS(pjsuaJNI.PJSIP_AC_AMBIGUOUS_get()),
  PJSIP_SC_BUSY_HERE(pjsuaJNI.PJSIP_SC_BUSY_HERE_get()),
  PJSIP_SC_REQUEST_TERMINATED(pjsuaJNI.PJSIP_SC_REQUEST_TERMINATED_get()),
  PJSIP_SC_NOT_ACCEPTABLE_HERE(pjsuaJNI.PJSIP_SC_NOT_ACCEPTABLE_HERE_get()),
  PJSIP_SC_BAD_EVENT(pjsuaJNI.PJSIP_SC_BAD_EVENT_get()),
  PJSIP_SC_REQUEST_UPDATED(pjsuaJNI.PJSIP_SC_REQUEST_UPDATED_get()),
  PJSIP_SC_REQUEST_PENDING(pjsuaJNI.PJSIP_SC_REQUEST_PENDING_get()),
  PJSIP_SC_UNDECIPHERABLE(pjsuaJNI.PJSIP_SC_UNDECIPHERABLE_get()),
  PJSIP_SC_INTERNAL_SERVER_ERROR(pjsuaJNI.PJSIP_SC_INTERNAL_SERVER_ERROR_get()),
  PJSIP_SC_NOT_IMPLEMENTED(pjsuaJNI.PJSIP_SC_NOT_IMPLEMENTED_get()),
  PJSIP_SC_BAD_GATEWAY(pjsuaJNI.PJSIP_SC_BAD_GATEWAY_get()),
  PJSIP_SC_SERVICE_UNAVAILABLE(pjsuaJNI.PJSIP_SC_SERVICE_UNAVAILABLE_get()),
  PJSIP_SC_SERVER_TIMEOUT(pjsuaJNI.PJSIP_SC_SERVER_TIMEOUT_get()),
  PJSIP_SC_VERSION_NOT_SUPPORTED(pjsuaJNI.PJSIP_SC_VERSION_NOT_SUPPORTED_get()),
  PJSIP_SC_MESSAGE_TOO_LARGE(pjsuaJNI.PJSIP_SC_MESSAGE_TOO_LARGE_get()),
  PJSIP_SC_PRECONDITION_FAILURE(pjsuaJNI.PJSIP_SC_PRECONDITION_FAILURE_get()),
  PJSIP_SC_BUSY_EVERYWHERE(pjsuaJNI.PJSIP_SC_BUSY_EVERYWHERE_get()),
  PJSIP_SC_DECLINE(pjsuaJNI.PJSIP_SC_DECLINE_get()),
  PJSIP_SC_DOES_NOT_EXIST_ANYWHERE(pjsuaJNI.PJSIP_SC_DOES_NOT_EXIST_ANYWHERE_get()),
  PJSIP_SC_NOT_ACCEPTABLE_ANYWHERE(pjsuaJNI.PJSIP_SC_NOT_ACCEPTABLE_ANYWHERE_get()),
  PJSIP_SC_TSX_TIMEOUT(pjsuaJNI.PJSIP_SC_TSX_TIMEOUT_get()),
  PJSIP_SC_TSX_TRANSPORT_ERROR(pjsuaJNI.PJSIP_SC_TSX_TRANSPORT_ERROR_get());

  public final int swigValue() {
    return swigValue;
  }

  public static pjsip_status_code swigToEnum(int swigValue) {
    pjsip_status_code[] swigValues = pjsip_status_code.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsip_status_code swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsip_status_code.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private pjsip_status_code() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private pjsip_status_code(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private pjsip_status_code(pjsip_status_code swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

