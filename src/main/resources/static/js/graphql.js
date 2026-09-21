async function graphqlRequest(query, variables = {}) {
    try {
        const response = await fetch('/graphql', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ query, variables })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        if (result.errors && result.errors.length > 0) {
            const errorMsg = result.errors.map(err => err.message).join('; ');
            throw new Error(errorMsg);
        }

        return result.data;
    } catch (err) {
        console.error('GraphQL request failed:', err);
        throw err;
    }
}
