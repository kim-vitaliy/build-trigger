package com.jetbrains.buildtrigger.stub

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.Stubbing

/**
 * Стаб для обращения к Git
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class GitStub(private val stubbing: Stubbing) {

    companion object {
        val URL_PATTERN = "http://127.0.0.1:${System.getProperty("testJettyStubsPort")}/lsRemoteRepository"
    }

    fun addLsRemoteRepositorySingleBranchStub(branch: String,
                                              lastCommit: String = "464952446a8a4bbec7b0e683a6d66f6721d015c5") {
        val response = "001e# service=git-upload-pack\n" +
                "000001548c3c903df2520030491b8072a8d7482f683611a7 HEAD\u0000multi_ack thin-pack side-band side-band-64k ofs-delta shallow deepen-since deepen-not deepen-relative no-progress include-tag multi_ack_detailed allow-tip-sha1-in-want allow-reachable-sha1-in-want no-done symref=HEAD:refs/heads/main filter object-format=sha1 agent=git/github-ga61f8f187ea2\n" +
                "0046557ce29c2575c878d67690d52d442654cc2c3f88 refs/heads/feature/RKO-1\n" +
                "003d8c3c903df2520030491b8072a8d7482f683611a7 refs/heads/main\n" +
                "0044${lastCommit} ${branch}\n" +
                "0000"

        stubbing.stubFor(WireMock.get(WireMock.urlPathMatching("/lsRemoteRepository/.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/x-git-upload-pack-advertisement")
                        .withBody(response)))
    }
}