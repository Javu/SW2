/**
 * This is the source package for the fantasyteam.ft1.networkingbase package,
 * see description for list of features and instructions on using the package.
 * <p>
 * The fantasyteam.ft1.networkingbase packages is a networking suite for use as
 * a networking plug-in for the FT1 engine. It extends upon the
 * {@link fantasyteam.ft1.Networking} class to implement a client-server socket
 * style networking package using the features of java.net to make an IPv4 based
 * networking system. This implementation is threaded, using a new thread for
 * each individual connection and a new thread for each connections message
 * queue (more on the message queue system below). The networkingbase package
 * extends upon the basics of a client/server networking architecture by
 * implementing the below advanced features to aid a more robust, stable and
 * seamless networking system.
 * </p>
 * <table summary="list of features"><tr><td align="center"><strong>List of
 * Networking Features</strong></td></tr>
 * <tr><td><strong>Summary</strong></td></tr><tr><td>The below features are
 * advanced features built on the top of a bare bones socket based system to aid
 * in the stability and usability of the network. Each feature can be turn on by
 * setting a boolean flag in the {@link Server} class to true. All these
 * features can be turned off and on freely and the effects will be applied
 * retroactively to all current sockets, not just newly created sockets. Each
 * feature will be listed as
 * follows:</td></tr><tr><td>&nbsp;</td></tr><tr><td><strong>Name of
 * Feature</strong></td></tr><tr><td>Function used on server to set the boolean
 * flag to true or false, turning the feature on or
 * off</td></tr><tr><td>Description of feature and how to use it</td></tr>
 * <tr><td>&nbsp;</td></tr><tr><td><strong>Disconnect/Reconnect</strong></td></tr><tr><td>void
 * {@link Server}.setUseDisconnectedSockets(boolean use)</td></tr><tr><td><p>
 * This feature is used to track connections that have disconnected. These
 * disconnected connections can then attempt to reconnect to the server and
 * re-establish a connection, meaning that the player will not be completely
 * unable to continue the game if they have the misfortune of
 * disconnecting.</p><p>
 * This feature alone just simply logs each disconnected connection, adding the
 * connections identifier to a list when they disconnect and removing them from
 * the list and realigning their data when they reconnect, regardless of how
 * long the player has been disconnected for. However if the Message Queue
 * feature is turned on then each socket can be assigned a timeout value,
 * causing the player to be unable to reconnect if they do not reconnect within
 * the given timeout. You can set this timeout value globally for all sockets
 * using the {@link Server}.setQueueTimeoutDisconnect function, or you can set
 * individual timeout values on different sockets using the
 * {@link Server}.setQueueTimeoutDisconnectIndividual function. See the Message
 * Queue feature below for more information.</p></td></tr>
 * <tr><td>&nbsp;</td></tr><tr><td><strong>Message
 * Queue</strong></td></tr><tr><td>void
 * {@link Server}.setUseMessageQueues(boolean use)</td></tr><tr><td><p>
 * This function causes the {@link Server} to create a new {@link MessageQueue}
 * thread for each {@link SocketThread}. Primarily, this thread is used to send
 * messages on behalf of the SocketThread. The SocketThread simply adds messages
 * to its queue and the MessageQueue loops through its queue attempting to send
 * each message to the remote Server. This queue has a range of options that can
 * be used to aid in correct message handling, such as pausing the message queue
 * if needed and resuming the queue, clearing the message queue of all queued
 * messages and it can work in tandem with the Disconnect/Reconnect feature to
 * time out disconnected sockets after a given timeout value, not allowing them
 * to attempt to reconnect if they take too long. It also gives the socket the
 * option of recovering from IO errors on the socket, rather than just
 * dieing.</p></td></tr>
 * <tr><td>&nbsp;</td></tr><tr><td><strong>Confirm
 * Connection</strong></td></tr><tr><td>void
 * {@link Server}.setUseConnectionConfirmation(boolean use)</td></tr><tr><td><p>
 * The confirm connection feature allows a client to wait for confirmation from
 * the server that a connection has been successfully established. This stops
 * the client from attempting to make a connection, then mindlessly begin
 * processing, sending and attempting to receive messages when the server is not
 * ready or has not successfully established a connection.
 * </p>
 * <p>
 * The client will however wait forever until it detects activity on the socket
 * before trying to process anything else. To allow the socket to periodically
 * break input blocking and process other things use the socket timeout feature.
 * This allows the confirm connection feature to also close the socket and throw
 * an exception if the confirmation message is not received in time, allowing
 * appropriate action to be taken.</p></td></tr>
 * <tr><td>&nbsp;</td></tr><tr><td><strong>Socket
 * Timeout</strong></td></tr><tr><td>void
 * {@link Server}.setUseSocketTimeout(boolean use)</td></tr><tr><td><p>
 * This feature allows the socket to break input blocking on the input stream if
 * nothing is received on the stream for a period of time. This allows the
 * socket to continue processing other things instead of waiting on input
 * forever.
 * </p>
 * <p>
 * The feature is also built to close the socket and throw an exception if input
 * blocking is broken too many consecutive times by the feature, essentially
 * detecting whether there is no activity on the socket and allowing the user to
 * handle this as needed. To set the timeout value (in ms) determining how long
 * to wait for input before breaking blocking use the setSocketTimeout function
 * of Server. To set the number of consecutive times to break input blocking
 * before closing the socket use the setSocketTimeoutCount function of Server.
 * if you do not want the socket to close after a number of consecutive timeouts
 * then set this value to -1.
 * </p></td></tr>
 * <tr><td>&nbsp;</td></tr><tr><td><strong>Multiple games handled by the one
 * Server</strong></td></tr><tr><td>void {@link Server}.setSocketGame(String
 * hash, int game)</td></tr><tr><td><p>
 * This feature allows a single server to concurrently handle multiple instances
 * of the same game by grouping each {@link SocketThread} by an integer value.
 * This allows the server to know which connections belong to which group, or
 * instance of the game. This is especially useful for creating a dedicated
 * server for multiplayer games.
 * </p></td></tr>
 * </table>
 */
package fantasyteam.ft1.networkingbase;
