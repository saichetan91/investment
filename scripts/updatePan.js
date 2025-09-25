const mysql = require('mysql2');
const crypto = require('crypto');

const dbConfig = {
    host: 'mariadb.stage.tap.priv',
    user: 'chetan',
    password: 'chetan@tap',
    database: 'user',
    port: 3306
};

// Function to generate random lowercase PAN
function generateRandomPan() {
    const length = 10; // Standard PAN length
    return crypto.randomBytes(length)
        .toString('base64')
        .toLowerCase()
        .replace(/[^a-z]/g, '') // Keep only lowercase letters
        .slice(0, length);
}

// Function to update PAN for a specific user
async function updateUserPan(userId) {
    const connection = mysql.createConnection(dbConfig);
    
    try {
        // Connect to database
        await connection.promise().connect();
        console.log('Connected to database successfully');

        // Generate new random PAN
        const newPan = generateRandomPan();
        
        // Update query
        const updateQuery = 'UPDATE user.profile SET pan = ? WHERE user_id = ?';
        const [result] = await connection.promise().execute(updateQuery, [newPan, userId]);
        
        if (result.affectedRows > 0) {
            console.log(`Successfully updated PAN to: ${newPan} for user: ${userId}`);
        } else {
            console.log(`No user found with ID: ${userId}`);
        }

    } catch (error) {
        console.error('Error updating PAN:', error.message);
        throw error;
    } finally {
        // Close the connection
        connection.end();
        console.log('Database connection closed');
    }
}


if (require.main === module) {
    const userId = process.argv[2];
    
    if (!userId) {
        console.error('Please provide a user ID as an argument');
        process.exit(1);
    }

    updateUserPan(parseInt(userId))
        .then(() => process.exit(0))
        .catch((error) => {
            console.error('Failed to update PAN:', error);
            process.exit(1);
        });
}

module.exports = { updateUserPan, generateRandomPan };