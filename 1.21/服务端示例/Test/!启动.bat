: 确保你是jdk21+
for %%F in (".\arclight-forge-1.21*.jar") do (
	java -jar %%F --nogui
    )
)

pause