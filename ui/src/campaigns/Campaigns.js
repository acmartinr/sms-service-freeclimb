import React, { Component } from "react";
import { isMobile } from "react-device-detect";
import { Redirect } from "react-router-dom";

import "./Campaigns.css";
import "../common/Common.css";

import CampaignsAPI from "../campaigns/CampaignsAPI";
import Session from "../common/Session";
import RemoveModal from "../common/RemoveModal";

import CampaignDetailsModal from "./CampaignDetailsModal";
import CampaignListsModal from "./CampaignListsModal";
import CampaignTestSMSModal from "./CampaignTestSMSModal";
import CampaignErrorsModal from "./CampaignErrorsModal";
import CampaignLoginModal from "./CampaignLoginModal";

import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TablePagination from "@material-ui/core/TablePagination";
import Edit from "@material-ui/icons/Edit";
import Delete from "@material-ui/icons/Delete";
import Button from "@material-ui/core/Button";
import Play from "@material-ui/icons/PlayArrow";
import Pause from "@material-ui/icons/Pause";
import List from "@material-ui/icons/List";
import Error from "@material-ui/icons/Error";
import Tooltip from "@material-ui/core/Tooltip";
import Message from "@material-ui/icons/Message";
import ExitToApp from "@material-ui/icons/ExitToApp";
import { InputLabel, MenuItem, Select } from "@material-ui/core";

/*import DateFnsUtils from '@date-io/date-fns';
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from '@material-ui/pickers';*/

class Campaigns extends Component {
  campaigntype = "SMS";
  constructor(props) {
    super(props);

    this.state = {
      selectedCampaign: {},
      campaigns: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      dateFrom: new Date(new Date().getTime() - 365 * 24 * 60 * 60 * 1000),
      dateTo: new Date(),
      showAddCampaignModal: false,
      showRemoveCampaignModal: false,
      showCampaignListsModal: false,
      showTestSMSModal: false,
      showErrorsModal: false,
      updateIntervalId: "",
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.showAddCampaignModal = this.showAddCampaignModal.bind(this);
    this.closeEditModal = this.closeEditModal.bind(this);
    this.editCampaign = this.editCampaign.bind(this);
    this.removeSelectedCampaign = this.removeSelectedCampaign.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeCampaign = this.removeCampaign.bind(this);
    this.showTestSMSModal = this.showTestSMSModal.bind(this);
    this.closeTestSMSModal = this.closeTestSMSModal.bind(this);

    this.handleStartDateChanged = this.handleStartDateChanged.bind(this);
    this.handleEndDateChanged = this.handleEndDateChanged.bind(this);

    this.changeLists = this.changeLists.bind(this);
    this.closeListsModal = this.closeListsModal.bind(this);

    this.startCampaign = this.startCampaign.bind(this);
    this.stopCampaign = this.stopCampaign.bind(this);
    this.closeErrorsModal = this.closeErrorsModal.bind(this);
    this.closeCampaignLoginModal = this.closeCampaignLoginModal.bind(this);
  }

  changeLists(campaign) {
    this.setState({ showCampaignListsModal: true, selectedCampaign: campaign });
  }

  closeListsModal() {
    this.setState({ showCampaignListsModal: false, selectedCampaign: {} });
    this.updateCampaigns();
  }

  removeSelectedCampaign() {
    CampaignsAPI.removeCampaign(this.state.selectedCampaign, (response) => {
      if (response.status === "OK") {
        this.closeRemoveModal();
        this.updateCampaigns();
      }
    });
  }

  handleStartDateChanged(date) {
    this.setState({ dateFrom: date, page: 0 }, function () {
      this.updateCampaigns();
    });
  }

  handleEndDateChanged(date) {
    this.setState({ dateTo: date, page: 0 }, function () {
      this.updateCampaigns();
    });
  }

  removeCampaign(campaign) {
    this.setState({
      selectedCampaign: campaign,
      showRemoveCampaignModal: true,
      title: "Remove campaign",
      message: "Are you sure you want to remove this campaign?",
    });
  }

  closeRemoveModal(campaign) {
    this.setState({ selectedCampaign: {}, showRemoveCampaignModal: false });
  }

  closeErrorsModal(campaign) {
    this.updateCampaigns();
    this.setState({ selectedCampaign: {}, showErrorsModal: false });
  }

  editCampaign(campaign) {
    this.setState({ selectedCampaign: campaign, showAddCampaignModal: true });
  }

  showAddCampaignModal() {
    this.setState({ showAddCampaignModal: true });
  }

  closeEditModal() {
    this.setState({
      selectedCampaign: {},
      showAddCampaignModal: false,
      page: 0,
    });
    this.updateCampaigns();
  }

  showTestSMSModal(campaign) {
    this.setState({ selectedCampaign: campaign, showTestSMSModal: true });
  }

  showErrorsModal(campaign) {
    if (campaign.errorsCount > 0) {
      this.setState({ selectedCampaign: campaign, showErrorsModal: true });
    }
  }

  showCampaignLoginModal(campaign) {
    this.setState({ selectedCampaign: campaign, showCampaignLoginModal: true });
  }

  closeCampaignLoginModal() {
    this.setState({ selectedCampaign: {}, showCampaignLoginModal: false });
    this.updateCampaigns();
  }

  closeTestSMSModal() {
    this.setState({ selectedCampaign: {}, showTestSMSModal: false });
  }

  componentDidMount() {
    this.updateCampaigns();
    this.scheduleCampaignsUpdate();
  }

  componentWillUnmount() {
    if (this.state.updateIntervalId) {
      clearInterval(this.state.updateIntervalId);
    }
  }

  scheduleCampaignsUpdate() {
    var that = this;

    const intervalId = setInterval(function () {
      that.updateCampaigns();
    }, 5000);
    this.setState({ updateIntervalId: intervalId });
  }

  handleChangePage(event, value) {
    this.setState({ page: value }, this.updateCampaigns);
  }

  handleChangeRowsPerPage(event) {
    this.setState({ rowsPerPage: event.target.value }, this.updateCampaigns);
  }

  prettyDate(dateMS, full) {
    if (dateMS > 0) {
      const date = new Date(dateMS);

      if (full) {
        return (
          date.toLocaleDateString(date) + " " + date.toLocaleTimeString(date)
        );
      } else {
        return date.toLocaleDateString(date);
      }
    } else {
      return "empty";
    }
  }

  startDay(date) {
    return new Date(date.toDateString(date));
  }

  startCampaign(campaign) {
    CampaignsAPI.startCampaign(campaign, (response) => {
      if (response.status === "OK") {
        this.updateCampaigns();
      }
    });
  }

  stopCampaign(campaign) {
    CampaignsAPI.stopCampaign(campaign, (response) => {
      if (response.status === "OK") {
        this.updateCampaigns();
      }
    });
  }

  updateCampaigns() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search,
      dateFrom: this.startDay(this.state.dateFrom).getTime(),
      dateTo: this.startDay(this.state.dateTo).getTime() + 24 * 60 * 60 * 1000,
    };

    CampaignsAPI.getCampaigns(request, (response) => {
      if (response.status === "OK") {
        this.setState({
          campaigns: response.data.campaigns,
          count: response.data.count,
        });
      }
    });
  }

  isAgentsLoginEnabled() {
    return Session.getUser().agentsLoginEnabled === "1";
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }
  


  render() {
    if (Session.isAuthenticated() && this.isLimitedUser()) {
      return <Redirect to="/chat" />;
    }

    return (
      <div className="padding-top-10">
        {/*<MuiPickersUtilsProvider utils={DateFnsUtils}>
          <KeyboardDatePicker
            disableToolbar
            variant="inline"
            className="inline padding-right-10 no-margin"
            format="MM/dd/yyyy"
            margin="normal"
            id="date-picker-inline"
            label="Date from"
            value={this.state.dateFrom}
            onChange={this.handleStartDateChanged}
            KeyboardButtonProps={{
              'aria-label': 'change date',
            }}/>

          <KeyboardDatePicker
            disableToolbar
            variant="inline"
            className="inline no-margin"
            format="MM/dd/yyyy"
            margin="normal"
            id="date-picker-inline"
            label="Date to"
            value={this.state.dateTo}
            onChange={this.handleEndDateChanged}
            KeyboardButtonProps={{
              'aria-label': 'change date',
            }}/>
          </MuiPickersUtilsProvider>*/}

        {!isMobile && !this.isLimitedUser() && (
          <Button
            type="button"
            variant="contained"
            color="primary"
            onClick={(e) => this.showAddCampaignModal()}
            className="right"
          >
            Add New Campaign
          </Button>
        )}

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Leads</TableCell>
              <TableCell>Sent</TableCell>
              {!isMobile && <TableCell>DNC</TableCell>}
              {!isMobile && <TableCell>Errors</TableCell>}
              {!isMobile && <TableCell>Ignored</TableCell>}
              {!isMobile && <TableCell>Created Date</TableCell>}
              <TableCell
                className={
                  isMobile || this.isLimitedUser() ? "" : "large-action-cell"
                }
              >
                Actions
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.campaigns.map((row) => (
              <TableRow key={row.id}>
                <TableCell>{row.name}</TableCell>
                <TableCell>{row.leadsCount}</TableCell>
                <TableCell>{row.sentCount}</TableCell>
                {!isMobile && <TableCell>{row.dncCount}</TableCell>}
                {!isMobile && (
                  <TableCell
                    className="pointer"
                    onClick={(e) => this.showErrorsModal(row)}
                  >
                    {row.errorsCount}
                  </TableCell>
                )}
                {!isMobile && <TableCell>{row.ignoredCount}</TableCell>}
                {!isMobile && (
                  <TableCell>{this.prettyDate(row.date, true)}</TableCell>
                )}
                <TableCell>
                  {row.errorStatus && (
                    <Tooltip title={row.errorStatus}>
                      <Error
                        className="pointer right-margin-10"
                        color="error"
                      />
                    </Tooltip>
                  )}
                  {row.status === 0 && (
                    <Tooltip title="Start Campaign">
                      <Play
                        className="pointer right-margin-10"
                        color="primary"
                        onClick={(e) => this.startCampaign(row)}
                      />
                    </Tooltip>
                  )}
                  {row.status === 1 && (
                    <Tooltip title="Pause Campaign">
                      <Pause
                        className="pointer right-margin-10"
                        color="primary"
                        onClick={(e) => this.stopCampaign(row)}
                      />
                    </Tooltip>
                  )}
                  {!isMobile && !this.isLimitedUser() && (
                    <React.Fragment>
                      <Tooltip title="Edit Campaign">
                        <Edit
                          className="pointer right-margin-10"
                          color="primary"
                          onClick={(e) => this.editCampaign(row)}
                        />
                      </Tooltip>

                      <Tooltip title="Attached List">
                        <List
                          className="pointer right-margin-10"
                          color="primary"
                          onClick={(e) => this.changeLists(row)}
                        />
                      </Tooltip>

                      <Tooltip title="Send Test Message">
                        <Message
                          className="pointer right-margin-10"
                          color="primary"
                          onClick={(e) => this.showTestSMSModal(row)}
                        />
                      </Tooltip>

                      {this.isAgentsLoginEnabled() && (
                        <Tooltip title="Update agent credentials">
                          <ExitToApp
                            className="pointer right-margin-10"
                            color="primary"
                            onClick={(e) => this.showCampaignLoginModal(row)}
                          />
                        </Tooltip>
                      )}

                      <Tooltip title="Delete Campaign">
                        <Delete
                          className="pointer"
                          color="primary"
                          onClick={(e) => this.removeCampaign(row)}
                        />
                      </Tooltip>
                    </React.Fragment>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            "aria-label": "Previous Page",
          }}
          nextIconButtonProps={{
            "aria-label": "Next Page",
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

        {this.state.showAddCampaignModal && (
          <CampaignDetailsModal
            selectedCampaign={this.state.selectedCampaign}
            handleClose={this.closeEditModal}
          />
        )}

        {this.state.showTestSMSModal && (
          <CampaignTestSMSModal
            selectedCampaign={this.state.selectedCampaign}
            handleClose={this.closeTestSMSModal}
          />
        )}

        {this.state.showCampaignListsModal && (
          <CampaignListsModal
            selectedCampaign={this.state.selectedCampaign}
            handleClose={this.closeListsModal}
          />
        )}

        {this.state.showRemoveCampaignModal && (
          <RemoveModal
            title={this.state.title}
            message={this.state.message}
            remove={this.removeSelectedCampaign}
            close={this.closeRemoveModal}
          />
        )}

        {this.state.showErrorsModal && (
          <CampaignErrorsModal
            selectedCampaign={this.state.selectedCampaign}
            handleClose={this.closeErrorsModal}
          />
        )}

        {this.state.showCampaignLoginModal && (
          <CampaignLoginModal
            selectedCampaign={this.state.selectedCampaign}
            handleClose={this.closeCampaignLoginModal}
          />
        )}
      </div>
    );
  }
}

export default Campaigns;
