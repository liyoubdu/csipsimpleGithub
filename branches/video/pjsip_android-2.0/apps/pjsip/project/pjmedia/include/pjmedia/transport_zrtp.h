/* $Id$ */
/*
  Copyright (C) 2010 Werner Dittmann

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef __PJMEDIA_TRANSPORT_ZRTP_H__
#define __PJMEDIA_TRANSPORT_ZRTP_H__

/**
 * @file transport_zrtp.h
 * @brief ZRTP Media Transport Adapter
 */

/* transport.h includes types.h -> config.h -> config_auto.h */
#include <pjmedia/transport.h>

PJ_BEGIN_DECL

/**
 * ZRTP option.
 */
typedef enum pjmedia_zrtp_use
{
    /** When this flag is specified, ZRTP will be disabled. */
    PJMEDIA_NO_ZRTP  = 1,

    /** When this flag is specified, PJSUA-LIB creates a ZRTP transport
     * call calls back the applicaion for further process if callback is
     * set.
     */
    PJMEDIA_CREATE_ZRTP  = 2

} pjmedia_zrtp_use;

PJ_END_DECL

#if defined(PJMEDIA_HAS_ZRTP) && PJMEDIA_HAS_ZRTP!=0
#include <libzrtpcpp/ZrtpCWrapper.h>

#define MAX_RTP_BUFFER_LEN  1024

/**
 * @defgroup PJMEDIA_TRANSPORT_ZRTP ZRTP Transport Adapter
 * @ingroup PJMEDIA_TRANSPORT
 * @brief This the ZRTP transport adapter.
 * @{
 *
 * PJMEDIA extension to support GNU ZRTP.
 *
 * ZRTP was developed by Phil Zimmermann and provides functions to
 * negotiate keys and other necessary data (crypto data) to set-up
 * the Secure RTP (SRTP) crypto context. Refer to Phil's ZRTP
 * specification at his <a href="http://zfoneproject.com/">Zfone
 * project</a> site to get more detailed information about the
 * capabilities of ZRTP.
 *
 * <b>Short overview of the ZRTP implementation</b>
 *
 * ZRTP is a specific protocol to negotiate encryption algorithms
 * and the required key material. ZRTP uses a RTP session to
 * exchange its protocol messages. Thus ZRTP is independent of any
 * signaling protocol like SIP, XMPP and alike.
 *
 * A complete GNU ZRTP implementation consists of two parts, the
 * GNU ZRTP core and some specific code that binds the GNU ZRTP core to
 * the underlying RTP/SRTP stack and the operating system:
 * <ul>
 * <li>
 *      The GNU ZRTP core is independent of a specific RTP/SRTP
 *      stack and the operationg system and consists of the ZRTP
 *      protocol state engine, the ZRTP protocol messages, and the
 *      GNU ZRTP engine. The GNU ZRTP engine provides methods to
 *      setup ZRTP message and to analyze received ZRTP messages,
 *      to compute the crypto data required for SRTP, and to
 *      maintain the required hashes and HMAC.
 * </li>
 * <li>
 *      The second part of an implementation is specific
 *      <em>glue</em> code the binds the GNU ZRTP core to the
 *      actual RTP/SRTP implementation and other operating system
 *      specific services such as timers, mutexes.
 * </li>
 * </ul>
 *
 * The GNU ZRTP core uses callback methods (refer to
 * zrtp_Callback) to access RTP/SRTP or operating specific methods,
 * for example to send data via the RTP stack, to access
 * timers, provide mutex handling, and to report events to the
 * application.
 *
 * <b>The PJMEDIA ZRTP transport</b>
 *
 * ZRTP transport implements code that is specific to the pjmedia
 * implementation. ZRTP transport also implements the specific code to
 * provide the mutex and timeout handling to the GNU ZRTP
 * core. Both, the mutex and the timeout handling, use the pjlib
 * library to stay independent of the operating
 * seystem.
 *
 * To perform its tasks ZRTP transport
 * <ul>
 * <li> implements the pjmedia transport functions and callbacks.
 * </li>
 * <li> implements the zrtp_Callbacks methods to provide
 *      access and other specific services (timer, mutex) to GNU
 *      ZRTP
 * </li>
 * <li> provides ZRTP specific methods that applications may use
 *      to control and setup GNU ZRTP
 * </li>
 * <li> can register and use an application specific callback
 *      class (refer to zrtp_UserCallbacks)
 * </li>
 * </ul>
 *
 * After instantiating a GNU ZRTP session (see below for a short
 * example) applications may use the methods of
 * ZRTP transport and the ZRTP engine to control and setup GNU ZRTP,
 * for example enable or disable ZRTP processing or getting ZRTP status
 * information.
 *
 * GNU ZRTP defines zrtp_UserCallback methods structure that an application
 * may use and register with ZRTP transport. GNU ZRTP and ZRTP transport
 * use the zrtp_UserCallback methods to report ZRTP events to the
 * application. The application may display this information to
 * the user or act otherwise.
 *
 * The following figure depicts the relationships between
 * ZRTP transport, pjmedia RTP implementation, the GNU ZRTP core,
 * SRTP and an application that provides zrtp_UserCallback methods.
 *
 @verbatim
                            +-----------+
                            |           |
                            | SRTP-ZRTP |
                            |           |
                            +-----------+
                            |C Wrapper  |
                            +-----+-----+
                                  |
                                  | uses
                                  |
  +-----------------+      +-------+--------+      +-+-----------------+
  |  App (pjsua)    |      |                |      |C|                 |
  |  creates a      | uses | transport_zrtp | uses | |    GNU ZRTP     |
  | ZRTP transport  +------+   implements   +------+W|      core       |
  | and implements  |      |  zrtp_Callback |      |r| implementation  |
  |zrtp_UserCallback|      |                |      |a|  (ZRtp et al)   |
  +-----------------+      +----------------+      |p|                 |
                                                   +-+-----------------+

@endverbatim
 *
 * The following short code snippet shows how to use ZRTP transport
 *
 * @code
 *
 * #include <pjmedia/transport_zrtp.h>
 * ...
 * // Create media transport
 * status = pjmedia_transport_udp_create(med_endpt, NULL, local_port,
 *                                       0, &transport);
 * if (status != PJ_SUCCESS)
 *     return status;
 *
 * status = pjmedia_transport_zrtp_create(med_endpt, NULL, transport,
 *                                        &zrtp_tp);
 * app_perror(THIS_FILE, "Error creating zrtp", status);
 * transport = zrtp_tp;
 * if (dir == PJMEDIA_DIR_ENCODING)
 *      pjmedia_transport_zrtp_initialize(transport, "testenc.zid", 1, NULL);
 * else
 *      pjmedia_transport_zrtp_initialize(transport, "testdec.zid", 1, NULL);
 * ...
 * @endcode
 *
 */

struct call;
PJ_BEGIN_DECL

/**
 * ZRTP option.
 */
typedef enum pjmedia_zrtp_use
{
    /** When this flag is specified, ZRTP will be disabled. */
    PJMEDIA_NO_ZRTP  = 1,

    /** When this flag is specified, PJSUA-LIB creates a ZRTP transport
     * call calls back the applicaion for further process if callback is
     * set.
     */
    PJMEDIA_CREATE_ZRTP  = 2
    
} pjmedia_zrtp_use;

/**
 * Create the transport adapter, specifying the underlying transport to be
 * used to send and receive RTP/RTCP packets.
 *
 * @param endpt     The media endpoint.
 * @param name      Optional name to identify this media transport
 *          for logging purposes.
 * @param transport The underlying media transport to send and receive
 *          RTP/RTCP packets.
 * @param p_tp      Pointer to receive the media transport instance.
 * 
 * @param close_slave
 *          Close the slave transport on transport_destroy. PJSUA-LIB
 *          sets this to PJ_FALSE because it takes care of this.
 *
 * @return      PJ_SUCCESS on success, or the appropriate error code.
 */
PJ_DECL(pj_status_t) pjmedia_transport_zrtp_create( pjmedia_endpt *endpt,
        const char *name,
        pjmedia_transport *transport,
        pjmedia_transport **p_tp,
        pj_bool_t close_slave);

/*
 * Implement the specific ZRTP transport functions
 */

/**
 * Initialize the ZRTP transport.
 *
 * Before an application can use ZRTP it has to initialize the
 * ZRTP implementation. This method opens a file that contains ZRTP specific
 * information such as the applications ZID (ZRTP id) and its
 * retained shared secrets.
 *
 * If one application requires several ZRTP sessions all
 * sessions use the same timeout thread and use the same ZID
 * file. Therefore an application does not need to do any
 * synchronisation regading ZID files or timeouts. This is
 * managed by the ZRTP implementation.
 *
 * The current implementation of ZRTP transport does not support
 * different ZID files for one application instance. This
 * restriction may be removed in later versions.
 *
 * The application may specify its own ZID file name. If no
 * ZID file name is specified it defaults to
 * <code>$HOME/.GNUccRTP.zid</code> if the <code>HOME</code>
 * environment variable is set. If it is not set the current
 * directory is used.
 *
 * If the method could set up the timeout thread and open the ZID
 * file then it enables ZRTP processing and returns.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @param zidFilename
 *     The name of the ZID file, can be a relative or absolut
 *     filename.
 *
 * @param autoEnable
 *     if set to true the method automatically sets enableZrtp to
 *     true. This enables the ZRTP auto-sense mode.
 *
 * @param config
 *     this parameter points to ZRTP configuration data. If it is
 *     NULL then ZRTP transport uses a default setting.
 *
 * @return
 *     PJ_SUCCESS on success, ZRTP processing enabled, other codes
 *     leave ZRTP processing disabled.
 *
 */
PJ_DECL(pj_status_t) pjmedia_transport_zrtp_initialize(pjmedia_transport *tp,
        const char *zidFilename,
        pj_bool_t autoEnable,
        void* config);
/**
 * Enable or disable ZRTP processing.
 *
 * Call this method to enable or disable ZRTP processing after
 * calling <code>pjmedia_transport_zrtp_initialize</code> with the
 * parameter @c autoEnable set to false. This can be done before
 * using a RTP session or at any time during a RTP session.
 *
 * Existing SRTP sessions or currently active ZRTP processing will
 * not be stopped or disconnected.
 *
 * If the application enables ZRTP then:
 * <ul>
 * <li>ZRTP transport starts to send ZRTP Hello packets after at least
 * one RTP packet was sent and received on the associated RTP
 * session. Thus if an application enables ZRTP and ZRTP transport
 * detects traffic on the RTP session then ZRTP transport automatically
 * starts the ZRTP protocol. This automatic start is convenient
 * for applications that negotiate RTP parameters and set up RTP
 * sessions but the actual RTP traffic starts some time later.
 * </li>
 * <li>ZRTP transport analyses incoming packets to detect ZRTP
 * messages. If ZRTP was started, either via automatic start (see
 * above) or explicitly via @c zrtp_startZrtp, then ZrtpQueue
 * forwards ZRTP packets to the GNU ZRTP core.
 * </ul>
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @param onOff
 *     @c 1 to enable ZRTP, @c 0 to disable ZRTP
 */
PJ_DECL(void) pjmedia_transport_zrtp_setEnableZrtp(pjmedia_transport *tp, pj_bool_t onOff);

/**
 * Return the state of ZRTP enable state.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @return @c true if ZRTP processing is enabled, @c false
 * otherwise.
 */
PJ_DECL(pj_bool_t) pjmedia_transport_zrtp_isEnableZrtp(pjmedia_transport *tp);

/**
 * Set the application's callback structure.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @param ucb
 *     Pointer the application's ZRTP C_UserCallbacks structure. Setting
 *     a NULL switches off the user callbacks
 */

PJ_DECL(void) pjmedia_transport_zrtp_setUserCallback(pjmedia_transport *tp, zrtp_UserCallbacks* ucb);

/**
 * Starts the ZRTP protocol engine.
 *
 * Applications may call this method to immediatly start the ZRTP protocol
 * engine any time after initializing ZRTP and setting optinal parameters,
 * for example client id or multi-stream parameters.
 *
 * If the application does not call this method but sucessfully initialized
 * the ZRTP engine using @c pjmedia_transport_zrtp_initialize then ZRTP may
 * also start, depending on the autoEnable parameter.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @see pjmedia_transport_zrtp_initialize
 */
PJ_DECL(void) pjmedia_transport_zrtp_startZrtp(pjmedia_transport *tp);

/**
 * Stops the ZRTP protocol engine.
 *
 * Applications call this method to stop the ZRTP protocol
 * engine. The ZRTP transport can not start or process any ZRTP
 * negotiations.
 *
 * This call does not deactivate SRTP processing of ZRTP transport, thus
 * the ZRTP transport still encrypts/decrypts data via SRTP.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 */
PJ_DECL(void) pjmedia_transport_zrtp_stopZrtp(pjmedia_transport *tp);

/**
 * Set the local SSRC in case of receive-only sessions.
 *
 * Receiver-only RTP sessions never send RTP packets, thus ZRTP cannot learn
 * the local (sender) SSRC. ZRTP requires the SSRC to bind the RTP session
 * to the SRTP and its handshake. In this case the application shall generate
 * a SSRC value and set it.
 *
 * Usually an application knows if a specific RTP session is receive-only, for
 * example by inspecting and parsing the SDP data.
 *
 * If the application later decides to switch this RTP session to full-duplex
 * mode (send and receive) it shall use the generated SSRC to intialize the
 * RTP session. Then the outgoing packets are encrypted by SRTP.
 *
 * @param tp
 *      Pointer to the ZRTP transport data as returned by
 *      @c pjmedia_transport_zrtp_create.
 *
 * @param ssrc
 *      The local ssrc value in host order.
 */
PJ_DECL(void) pjmedia_transport_zrtp_setLocalSSRC(pjmedia_transport *tp, uint32_t ssrc);

/**
 * Get the ZRTP context pointer.
 *
 * Appplications need the ZRTP context pointer if they call ZRTP specific
 * methods. The ZRTP specific include file @c ZrtpCWrapper contains the #
 * description of the ZRTP methods.
 *
 * @return Pointer to ZRTP context
 *
 * @see zrtp_setAuxSecret()
 * @see zrtp_setPbxSecret()
 * @see zrtp_inState()
 * @see zrtp_SASVerified()
 * @see zrtp_resetSASVerified()
 * @see zrtp_getHelloHash()
 * @see zrtp_getMultiStrParams()
 * @see zrtp_setMultiStrParams()
 * @see zrtp_isMultiStream()
 * @see zrtp_isMultiStreamAvailable()
 * @see zrtp_acceptEnrollment()
 * @see zrtp_setPBXEnrollment()
 * @see zrtp_setSignatureData()
 * @see zrtp_getSignatureData()
 * @see zrtp_getSignatureLength()
 * @see zrtp_getZid();
 */
PJ_DECL(ZrtpContext*) pjmedia_transport_zrtp_getZrtpContext(pjmedia_transport *tp);

PJ_END_DECL


/**
 * @}
 */
#endif

#endif  /* __PJMEDIA_TRANSPORT_ADAPTER_SAMPLE_H__ */


