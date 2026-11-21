import Foundation

/// Generic error surfaced by any feature's Kotlin bridge.
enum BridgeError: LocalizedError {
    case unknown
    case message(String)

    var errorDescription: String? {
        switch self {
        case .unknown: "Something went wrong."
        case .message(let text): text
        }
    }
}
