import SwiftUI

/// Root of the empty starter. The first real feature replaces this body with its own root view.
struct ContentView: View {
    var body: some View {
        DreamEmptyStateView(title: "placeholder_title", message: "placeholder_body")
    }
}

#Preview {
    ContentView()
}
