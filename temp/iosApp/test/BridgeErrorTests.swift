import XCTest
@testable import app

final class BridgeErrorTests: XCTestCase {
    func testUnknownErrorHasUserReadableDescription() {
        XCTAssertEqual(BridgeError.unknown.errorDescription, "Something went wrong.")
    }

    func testMessageErrorCarriesItsText() {
        XCTAssertEqual(BridgeError.message("Boom").errorDescription, "Boom")
    }
}
