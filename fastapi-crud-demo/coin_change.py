from typing import List

def coin_change_min_coins(coins: List[int], amount: int) -> int:
    """
    LeetCode 322: Coin Change (Minimum Coins)
    -----------------------------------------
    Find the fewest number of coins needed to make up a given amount.
    If that amount of money cannot be made up by any combination of the coins, return -1.

    Approach: Dynamic Programming (Bottom-Up / Tabulation)
    Time Complexity: O(amount * len(coins))
    Space Complexity: O(amount)
    """
    # Initialize DP array of size (amount + 1) filled with infinity
    # dp[i] represents the minimum coins needed to make up amount i
    dp = [float('inf')] * (amount + 1)
    
    # Base case: 0 coins needed to make amount 0
    dp[0] = 0
    
    # Iterate through all amounts from 1 to the target amount
    for i in range(1, amount + 1):
        for coin in coins:
            if i - coin >= 0:
                dp[i] = min(dp[i], dp[i - coin] + 1)
                
    # If dp[amount] is still infinity, it's impossible to make that amount
    return int(dp[amount]) if dp[amount] != float('inf') else -1


def coin_change_total_ways(coins: List[int], amount: int) -> int:
    """
    LeetCode 518: Coin Change II (Total Ways)
    -----------------------------------------
    Return the number of distinct combinations that make up the given amount.
    You may assume that you have an infinite number of each kind of coin.

    Approach: Dynamic Programming (Bottom-Up / Tabulation)
    Time Complexity: O(amount * len(coins))
    Space Complexity: O(amount)
    """
    # dp[i] represents the number of ways to make up amount i
    dp = [0] * (amount + 1)
    
    # Base case: There is exactly 1 way to make amount 0 (using no coins)
    dp[0] = 1
    
    # Loop over each coin first to avoid counting permutations (e.g., [1, 2] and [2, 1] as different ways)
    for coin in coins:
        for i in range(coin, amount + 1):
            dp[i] += dp[i - coin]
            
    return dp[amount]


if __name__ == "__main__":
    print("=" * 60)
    print("      COIN CHANGE PROBLEM SOLUTIONS (PYTHON)")
    print("=" * 60)
    
    # Test Data for Minimum Coins (Coin Change I)
    test_coins_1 = [1, 2, 5]
    test_amount_1 = 11
    min_coins = coin_change_min_coins(test_coins_1, test_amount_1)
    print(f"\n[Variant 1] Fewest Coins to make amount {test_amount_1} using coins {test_coins_1}:")
    print(f"--> Result: {min_coins} coins (Expected: 3, since 5 + 5 + 1 = 11)")
    
    test_coins_2 = [2]
    test_amount_2 = 3
    min_coins_invalid = coin_change_min_coins(test_coins_2, test_amount_2)
    print(f"\n[Variant 1] Fewest Coins to make amount {test_amount_2} using coins {test_coins_2}:")
    print(f"--> Result: {min_coins_invalid} (Expected: -1, as amount 3 cannot be made)")

    # Test Data for Total Ways (Coin Change II)
    test_coins_3 = [1, 2, 5]
    test_amount_3 = 5
    ways = coin_change_total_ways(test_coins_3, test_amount_3)
    print(f"\n[Variant 2] Total distinct ways to make amount {test_amount_3} using coins {test_coins_3}:")
    print(f"--> Result: {ways} ways (Expected: 4)")
    print("    Representations:")
    print("    1) 5")
    print("    2) 2 + 2 + 1")
    print("    3) 2 + 1 + 1 + 1")
    print("    4) 1 + 1 + 1 + 1 + 1")
    
    print("\n" + "=" * 60)
